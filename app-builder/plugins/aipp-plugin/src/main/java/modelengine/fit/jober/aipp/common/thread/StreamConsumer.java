/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.thread;

import modelengine.fitframework.log.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * 流处理器
 *
 * @author 陈潇文
 * @since 2024-12-18
 */
public class StreamConsumer extends Thread {
    private static final Logger log = Logger.get(StreamConsumer.class);

    InputStream is;

    public StreamConsumer(InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            byte data;
            int result;
            while ((result = is.read()) != -1) {
                data = (byte) result;
            }
        } catch (IOException e) {
            log.error("stream consumer read data failed.");
        }
    }
}
