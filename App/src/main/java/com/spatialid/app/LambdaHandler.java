// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;

import com.amazonaws.services.s3.AmazonS3URI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.dao.FacilityDataImportManagementHandler;
import com.spatialid.app.model.CheckErrorInfo;
import com.spatialid.app.model.FacilityDataImportManagementTaskBean;

/**
 * 設備データ整備完了受付(B2設備データ整備完了通知)クラス
 * 関数でB2設備データ整備の完了通知を行う
 * 
 * @author Ishii Yuki
 * @version 1.0
 */
public class LambdaHandler implements
		RequestHandler<ApplicationLoadBalancerRequestEvent, ApplicationLoadBalancerResponseEvent> {

	@Override
	public ApplicationLoadBalancerResponseEvent handleRequest(
			ApplicationLoadBalancerRequestEvent request, Context context) {
		// ログの出力(バッチ処理開始)
		Common.outputLogMessage("info.ChangeStatus.00001",
				Const.FUNCTION_NAME);

		FacilityDataImportManagementTaskBean task = null;
		CheckErrorInfo checkErrorInfo = new CheckErrorInfo(null);

		try {
			// 設定読込(プロパティファイル)
			Common.readProperty();
			// 設定読込(SecretManager)
			Common.readSecretManager();
		} catch (Exception e) {
			// ログの出力(システムエラー)
			Common.outputLogMessage("error.ChangeStatus.00001",
					Const.FUNCTION_NAME,
					Common.getErrorInfoString(e));
			return getResponse(Const.HTTP_500, Common.getLogMessage(
					"error.ChangeStatus.20001"));
		}

		try {
			// リクエスト情報取得
			task = getFacilityDataImportManagementTaskBean(request,
					checkErrorInfo);
		} catch (Exception e) {
			// ログの出力(リクエスト項目エラー)
			Common.outputLogMessage("error.ChangeStatus.00002",
					Const.FUNCTION_NAME,
					Common.getErrorInfoString(e));
			return getResponse(Const.HTTP_422,
					checkErrorInfo.getErrorString());
		}

		try (Connection connection = DriverManager.getConnection(
				Common.getDburl(), Common.getDbuser(),
				Common.getDbpassword())) {
			// 設備データ取込管理テーブル更新
			int updateCount = FacilityDataImportManagementHandler
					.executeUpdateFacilityDataImportManagementImportingStatement(
							connection,
							task);
			if (updateCount == 0) {
				return getResponse(Const.HTTP_404, Common
						.getLogMessage(
								"error.ChangeStatus.20002"));
			}
		} catch (Exception e) {
			// ログの出力(システムエラー)
			Common.outputLogMessage("error.ChangeStatus.00001",
					Const.FUNCTION_NAME,
					Common.getErrorInfoString(e));
			return getResponse(Const.HTTP_500, Common.getLogMessage(
					"error.ChangeStatus.20001"));
		}

		// ログの出力(バッチ処理正常終了)
		Common.outputLogMessage("info.ChangeStatus.00002",
				Const.FUNCTION_NAME);
		return getResponse(Const.HTTP_200, null);
	}

	/**
	 * レスポンスを取得する
	 * 
	 * @param httpStatus  HTTPステータスコード
	 * @param errorString エラー文
	 * @return レスポンス
	 */
	private ApplicationLoadBalancerResponseEvent getResponse(int httpStatus,
			String errorString) {
		ApplicationLoadBalancerResponseEvent res = new ApplicationLoadBalancerResponseEvent();
		// ヘッダを設定
		res.setStatusCode(httpStatus);

		// ボディを作成
		Map<String, Object> response = new LinkedHashMap<>();
		response.put(Const.RESPONSE_NAME_HTTPSTATUS, httpStatus);

		// エラー情報が存在する場合、エラー詳細を追加
		if (errorString != null) {
			Map<String, Object> errorDetail = new LinkedHashMap<>();
			errorDetail.put(Const.RESPONSE_NAME_DETAIL_MESSAGE,
					errorString);

			response.put(Const.RESPONSE_NAME_ERROR_DETAIL,
					new Object[] { errorDetail });
		}

		// ObjectMapperのインスタンスを作成
		ObjectMapper objectMapper = new ObjectMapper();
		String responseString = null;

		// MapをJSON文字列に変換
		try {
			responseString = objectMapper
					.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// ログの出力(レスポンス作成失敗)
			Common.outputLogMessage("error.ChangeStatus.10004",
					Common.getErrorInfoString(e));
			res.setStatusCode(Const.HTTP_500);
			return res;
		}

		// Content-Typeを設定
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("Content-Type", "application/json");
		res.setHeaders(headers);

		// ボディを設定
		res.setBody(responseString);

		return res;
	}

	/**
	 * リクエストから設備データ取込管理テーブル情報を取得する
	 * 
	 * @param request        リクエスト
	 * @param checkErrorInfo エラー情報
	 * @return 設備データ取込管理テーブル情報
	 * @throws Exception
	 */
	private FacilityDataImportManagementTaskBean getFacilityDataImportManagementTaskBean(
			ApplicationLoadBalancerRequestEvent request,
			CheckErrorInfo checkErrorInfo)
			throws Exception {

		JsonNode body = new ObjectMapper().readTree(request.getBody());
		Map<String, String> requestMap = new HashMap<>();

		requestMap.put(Const.ITEM_HIGHETS_VOXEL_SID.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_HIGHETS_VOXEL_SID.getPhysicalName()));
		requestMap.put(Const.ITEM_INFRA_COMPANY_ID.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_INFRA_COMPANY_ID.getPhysicalName()));
		requestMap.put(Const.ITEM_PRE_SID_DATA_FILE_URL.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_PRE_SID_DATA_FILE_URL.getPhysicalName()));
		requestMap.put(Const.ITEM_PRE_LINK_DATA_FILE_URL.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_PRE_LINK_DATA_FILE_URL.getPhysicalName()));
		requestMap.put(Const.ITEM_PRE_IMPORT_FILE_TIME.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_PRE_IMPORT_FILE_TIME.getPhysicalName()));
		requestMap.put(Const.ITEM_PRE_THREE_D_VIEWER_FILE_URL.getJapaneseName(),
				getRequestBodyItem(body, Const.ITEM_PRE_THREE_D_VIEWER_FILE_URL.getPhysicalName()));

		// 入力値チェック
		for (Map.Entry<String, String> input : requestMap.entrySet()) {
			// 必須チェック
			if (checkExistance(input.getValue())) {
				checkErrorInfo
						.setErrorString(Common.getLogMessage(
								"error.ChangeStatus.10001",
								input.getKey()));
				throw new Exception(Common.getLogMessage(
						"error.ChangeStatus.20004",
						input.getKey()));
			}
			// 桁数チェック
			// データセットの空間ID、インフラ事業者ID
			if (input.getKey() == Const.ITEM_HIGHETS_VOXEL_SID
					.getJapaneseName()) {
				if (checkDigits(input.getValue(),
						Const.MAX_DIGITS_HIGHEST_VOXEL_SID)) {
					checkErrorInfo.setErrorString(
							Common.getLogMessage(
									"error.ChangeStatus.10002",
									input.getKey()));
					throw new Exception(Common
							.getLogMessage(
									"error.ChangeStatus.20005",
									input.getKey()));
				}
			} else if (input.getKey() == Const.ITEM_INFRA_COMPANY_ID
					.getJapaneseName()) {
				if (checkDigits(input.getValue(),
						Const.MAX_DIGITS_INFRA_COMPANY_ID)) {
					checkErrorInfo.setErrorString(
							Common.getLogMessage(
									"error.ChangeStatus.10002",
									input.getKey()));
					throw new Exception(Common
							.getLogMessage(
									"error.ChangeStatus.20005",
									input.getKey()));
				}
			}
		}

		String highestVoxelSid = requestMap.get(
				Const.ITEM_HIGHETS_VOXEL_SID.getJapaneseName());
		String infraCompanyId = requestMap.get(
				Const.ITEM_INFRA_COMPANY_ID.getJapaneseName());
		AmazonS3URI preSidDataFileUrl = null;
		AmazonS3URI preLinkDataFileUrl = null;
		Timestamp preImportFileTime = null;
		AmazonS3URI preThreeDViewerFileUrl = null;

		// SidDataファイルURL形式チェック
		try {
			// csvファイル配置パス
			preSidDataFileUrl = new AmazonS3URI(
					requestMap.get(Const.ITEM_PRE_SID_DATA_FILE_URL
							.getJapaneseName()));
		} catch (Exception e) {
			checkErrorInfo.setErrorString(Common.getLogMessage(
					"error.ChangeStatus.10003",
					Const.ITEM_PRE_SID_DATA_FILE_URL
							.getJapaneseName()));
			throw new Exception(Common.getLogMessage(
					"error.ChangeStatus.20006",
					Const.ITEM_PRE_SID_DATA_FILE_URL
							.getJapaneseName()));
		}

		// LinkDataファイルURL形式チェック
		try {
			// csvファイル配置パス
			preLinkDataFileUrl = new AmazonS3URI(
					requestMap.get(Const.ITEM_PRE_LINK_DATA_FILE_URL
							.getJapaneseName()));
		} catch (Exception e) {
			checkErrorInfo.setErrorString(Common.getLogMessage(
					"error.ChangeStatus.10003",
					Const.ITEM_PRE_LINK_DATA_FILE_URL
							.getJapaneseName()));
			throw new Exception(Common.getLogMessage(
					"error.ChangeStatus.20006",
					Const.ITEM_PRE_LINK_DATA_FILE_URL
							.getJapaneseName()));
		}

		try {
			// ファイル登録日時
			String preImportFileTimeString = requestMap
					.get(Const.ITEM_PRE_IMPORT_FILE_TIME
							.getJapaneseName());
			preImportFileTime = new Timestamp(new SimpleDateFormat(
					Const.PRE_IMPORT_FILE_TIME_FORMAT)
					.parse(preImportFileTimeString)
					.getTime());
		} catch (Exception e) {
			checkErrorInfo.setErrorString(Common.getLogMessage(
					"error.ChangeStatus.10003",
					Const.ITEM_PRE_IMPORT_FILE_TIME
							.getJapaneseName()));
			throw new Exception(Common.getLogMessage(
					"error.ChangeStatus.20006",
					Const.ITEM_PRE_IMPORT_FILE_TIME
							.getJapaneseName()));
		}

		try {
			// 3DViewerファイルURL配置パス
			preThreeDViewerFileUrl = new AmazonS3URI(
					requestMap.get(Const.ITEM_PRE_THREE_D_VIEWER_FILE_URL
							.getJapaneseName()));
		} catch (Exception e) {
			checkErrorInfo.setErrorString(Common.getLogMessage(
					"error.ChangeStatus.10003",
					Const.ITEM_PRE_THREE_D_VIEWER_FILE_URL
							.getJapaneseName()));
			throw new Exception(Common.getLogMessage(
					"error.ChangeStatus.20006",
					Const.ITEM_PRE_THREE_D_VIEWER_FILE_URL
							.getJapaneseName()));
		}

		return new FacilityDataImportManagementTaskBean(highestVoxelSid,
				infraCompanyId,
				preSidDataFileUrl, preLinkDataFileUrl,
				preImportFileTime,
				preThreeDViewerFileUrl);
	}

	/**
	 * リクエストbodyから指定した値を取得する
	 * 
	 * @param body リクエストbody
	 * @param key  項目名
	 * @return 取得した値
	 */
	private String getRequestBodyItem(JsonNode body, String key) {
		try {
			return body.get(key).asText();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 値が空でないことを確認する
	 * 
	 * @param value 確認対象文字列
	 * @return 確認結果
	 */
	private boolean checkExistance(String value) {
		return (value == null || value.isEmpty());
	}

	/**
	 * 既定の桁数を超えていないことを確認する
	 * 
	 * @param value     確認対象文字列
	 * @param maxDigits 最大桁数
	 * @return 確認結果
	 */
	private boolean checkDigits(String value, int maxDigits) {
		return (value.length() > maxDigits);
	}
}
