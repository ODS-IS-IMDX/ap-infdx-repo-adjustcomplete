// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.model;

import lombok.Data;

/**
 * 取込状態クラス
 * 取込状態の情報(ID、ステータス)を格納する
 * 
 * @author Ishii Yuki
 * @version 1.0
 */
@Data
public class ImportStatusInfo {

    /**
     * 取込状態ID
     */
    private Integer id;

    /**
     * 取込状態名
     */
    private String status;

    public ImportStatusInfo(Integer id, String status) {
        this.id = id;
        this.status = status;
    }
}
