// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.model;

import java.sql.Timestamp;
import com.amazonaws.services.s3.AmazonS3URI;
import lombok.Data;

/**
 * 設備データ取込管理テーブル情報Beanクラス
 * 設備データ取込管理テーブルの情報を格納する
 * 
 * @author Ishii Yuki
 * @version 1.0
 */
@Data
public class FacilityDataImportManagementTaskBean {

    /**
     * データセットの空間ID
     */
    private String highestVoxelSid;

    /**
     * インフラ事業者ID
     */
    private String infraCompanyId;

    /**
     * SidDataファイルURL(取込前)
     */
    private AmazonS3URI preSidDataFileUrl;

    /**
     * LinkDataファイルURL(取込前)
     */
    private AmazonS3URI preLinkDataFileUrl;

    /**
     * 取込ファイル登録日時(取込前)
     */
    private Timestamp preImportFileTime;

    /**
     * 3DViewerファイルURL(取込前)
     */
    private AmazonS3URI preThreeDViewerFileUrl;

    public FacilityDataImportManagementTaskBean(String highestVoxelSid,
            String infraCompanyId,
            AmazonS3URI preSidDataFileUrl, AmazonS3URI preLinkDataFileUrl,
            Timestamp preImportFileTime, AmazonS3URI preThreeDViewerFileUrl) {
        this.highestVoxelSid = highestVoxelSid;
        this.infraCompanyId = infraCompanyId;
        this.preSidDataFileUrl = preSidDataFileUrl;
        this.preLinkDataFileUrl = preLinkDataFileUrl;
        this.preImportFileTime = preImportFileTime;
        this.preThreeDViewerFileUrl = preThreeDViewerFileUrl;
    }
}
