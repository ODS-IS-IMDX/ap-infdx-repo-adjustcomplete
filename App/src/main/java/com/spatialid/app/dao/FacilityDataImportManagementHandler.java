// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.spatialid.app.Common;
import com.spatialid.app.Const;
import com.spatialid.app.model.FacilityDataImportManagementTaskBean;

/**
 * 設備データ取込管理ハンドラークラス
 * 設備データ取込管理テーブルに対する操作を行う
 * 
 * @author Ishii Yuki
 * @version 1.0
 */
public class FacilityDataImportManagementHandler {
	/**
	 * B2設備データ整備完了通知SQL実行
	 * 
	 * @param connection 接続情報
	 * @param taskInfo   取込タスク情報
	 * @return B2設備データ整備完了通知SQL実行結果
	 * @throws SQLException
	 */
	public static int executeUpdateFacilityDataImportManagementImportingStatement(
			Connection connection,
			FacilityDataImportManagementTaskBean taskInfo)
			throws SQLException {

		PreparedStatement ps = null;
		int updateCount = 0;

		try {
			ps = prepareUpdateFacilityDataImportManagementImportingStatement(
					connection, taskInfo);
			updateCount = ps.executeUpdate();
		} catch (SQLException e) {
			// ログの出力(更新失敗)
			Common.outputLogMessage("error.ChangeStatus.00004",
					Common.getCurrentMethodName(),
					(ps == null ? "" : ps.toString()),
					Common.getErrorInfoString(e));
			throw e;
		}

		if (updateCount == 0) {
			// ログの出力(対象レコードなし)
			Common.outputLogMessage("error.ChangeStatus.00008",
					Common.getCurrentMethodName(),
					ps.toString());
		} else {
			// ログの出力(更新完了)
			Common.outputLogMessage("info.ChangeStatus.00006",
					Common.getCurrentMethodName(),
					ps.toString());
		}

		return updateCount;
	}

	/**
	 * B2設備データ整備完了通知SQL作成
	 * 
	 * @param connection 接続情報
	 * @param taskInfo   取込タスク情報
	 * @return B2設備データ整備完了通知SQL
	 * @throws SQLException
	 */
	public static PreparedStatement prepareUpdateFacilityDataImportManagementImportingStatement(
			Connection connection,
			FacilityDataImportManagementTaskBean taskInfo)
			throws SQLException {
		String sql = "update facility_data_import_management "
				+ "set pre_import_status = ?, pre_sid_data_file_url = ?,pre_link_data_file_url = ?, pre_import_file_time = ?, pre_three_d_viewer_file_url = ?, "
				+ "update_time = ? "
				+ "where highest_voxel_id = ? and infra_company_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setObject(1, Const.PRE_IMPORT_STATUS_NOT_CAPTURED.getId(),
				java.sql.Types.INTEGER);
		pstmt.setString(2, taskInfo.getPreSidDataFileUrl().toString());
		pstmt.setString(3, taskInfo.getPreLinkDataFileUrl().toString());
		pstmt.setTimestamp(4, taskInfo.getPreImportFileTime());
		pstmt.setString(5, taskInfo.getPreThreeDViewerFileUrl().toString());
		pstmt.setTimestamp(6,
				new Timestamp(System.currentTimeMillis()));

		pstmt.setString(7, taskInfo.getHighestVoxelSid());
		pstmt.setString(8, taskInfo.getInfraCompanyId());

		return pstmt;
	}
}
