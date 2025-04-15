// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app;

import com.spatialid.app.model.ImportStatusInfo;
import com.spatialid.app.model.requestItemInfo;

/**
 * 定数定義クラス
 * プロジェクト内で利用する定数を定義する
 * 
 * @author Ishii Yuki
 * @version 1.0
 */
public class Const {
	// プロパティファイル情報
	// プロパティファイルパス
	public static final String PROPERTY_FILE_PATH = "src/main/resources/application.properties";
	// プロパティ名(SecretManager接続情報(シークレットID))
	public static final String PROPERTY_NAME_SECLET_MANAGER_NAME = "secretManagerName";
	// プロパティ名(SecretManager接続情報(AWSリージョン))
	public static final String PROPERTY_NAME_SECRET_MANAGER_REGION = "secretManagerRegion";

	// // 設備データ取込管理テーブル
	// 取込状態(ID, 状態名)
	public static final ImportStatusInfo PRE_IMPORT_STATUS_NOT_CAPTURED = new ImportStatusInfo(12, "未取込データあり");

	// リクエスト形式(論理名, 物理名)
	public static final requestItemInfo ITEM_HIGHETS_VOXEL_SID = new requestItemInfo("空間ID", "highestVoxelId");
	public static final requestItemInfo ITEM_INFRA_COMPANY_ID = new requestItemInfo("インフラ事業者ID", "infraCompanyId");
	public static final requestItemInfo ITEM_PRE_SID_DATA_FILE_URL = new requestItemInfo("SidDataファイル配置パス",
			"preSidDataFileUrl");
	public static final requestItemInfo ITEM_PRE_LINK_DATA_FILE_URL = new requestItemInfo("LinkDataファイル配置パス",
			"preLinkDataFileUrl");
	public static final requestItemInfo ITEM_PRE_IMPORT_FILE_TIME = new requestItemInfo("ファイル登録日時",
			"preImportFileTime");
	public static final requestItemInfo ITEM_PRE_THREE_D_VIEWER_FILE_URL = new requestItemInfo("3DViewerファイル配置パス",
			"preThreeDViewerFileUrl");
	// ファイル登録日時のフォーマット
	public static final String PRE_IMPORT_FILE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

	// リクエスト値チェック情報
	// 最大桁数(データセットの空間ID)
	public static final int MAX_DIGITS_HIGHEST_VOXEL_SID = 100;
	// 最大桁数(インフラ事業者ID)
	public static final int MAX_DIGITS_INFRA_COMPANY_ID = 20;

	// レスポンス
	// 項目名(ステータス)
	public static final String RESPONSE_NAME_HTTPSTATUS = "status";
	// 項目名(エラー内容詳細)
	public static final String RESPONSE_NAME_ERROR_DETAIL = "errorDetail";
	// 項目名(詳細メッセージ)
	public static final String RESPONSE_NAME_DETAIL_MESSAGE = "detailMessage";
	// HTTPステータスコード
	public static final int HTTP_200 = 200;
	public static final int HTTP_404 = 404;
	public static final int HTTP_422 = 422;
	public static final int HTTP_500 = 500;

	// SecretManager情報
	// YugabyteDBのホスト名
	public static final String SECRET_KEY_YDB_HOST = "YDB-HOST";
	// YugabyteDBのポート
	public static final String SECRET_KEY_YDB_PORT = "YDB-PORT";
	// YugabyteDBのDB名
	public static final String SECRET_KEY_YDB_NAME = "YDB-NAME";
	// YugabyteDBのユーザ名
	public static final String SECRET_KEY_YDB_USER = "YDB-B1-USER";
	// YugabyteDBのユーザパスワード
	public static final String SECRET_KEY_YDB_PASS = "YDB-B1-USER-PASSWORD";
	// YugabyteDB接続情報のフォーマット
	public static final String YDB_URL_FORMAT = "jdbc:postgresql://{0}:{1}/{2}";

	// ログメッセージ
	// 関数名称
	public static final String FUNCTION_NAME = "B2設備データ整備完了通知";
}
