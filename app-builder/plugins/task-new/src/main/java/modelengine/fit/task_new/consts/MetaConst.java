/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.consts;

import static modelengine.fit.jober.aipp.constants.AippConst.INST_AGENT_RESULT_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CHILD_INSTANCE_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CREATE_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_DATA_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_VERSION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_FINISH_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_FLOW_INST_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_MODIFY_BY_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_MODIFY_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_NAME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_PROGRESS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_RESUME_DURATION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_SMART_FORM_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_STATUS_KEY;

import java.util.Arrays;
import java.util.List;

/**
 * Meta 相关常量
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
public class MetaConst {
    public static final List<String> PROPERTY_NAME_LIST = Arrays.asList(INST_NAME_KEY,
            INST_CREATE_TIME_KEY,
            INST_MODIFY_BY_KEY,
            INST_MODIFY_TIME_KEY,
            INST_FINISH_TIME_KEY,
            INST_FLOW_INST_ID_KEY,
            INST_CURR_FORM_ID_KEY,
            INST_CURR_FORM_VERSION_KEY,
            INST_CURR_FORM_DATA_KEY,
            INST_SMART_FORM_TIME_KEY,
            INST_RESUME_DURATION_KEY,
            INST_STATUS_KEY,
            INST_PROGRESS_KEY,
            INST_AGENT_RESULT_KEY,
            INST_CHILD_INSTANCE_ID,
            INST_CURR_NODE_ID_KEY);
}


