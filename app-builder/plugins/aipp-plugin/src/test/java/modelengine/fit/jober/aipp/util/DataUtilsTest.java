/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.exceptions.JobberException;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link DataUtils} 测试类
 *
 * @author lixin
 * @since 2025-01-08
 */
public class DataUtilsTest {
    @Test
    void testGetFilePathWithValidData() {
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("file_path", "path/to/file");
        String fileKey = "fileKey";
        businessData.put(fileKey, fileInfo);
        String filePath = DataUtils.getFilePath(businessData, fileKey);

        assertEquals("path/to/file", filePath);
    }

    @Test
    void testGetFilePathWithEmptyFilePath() {
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("file_path", "");
        String fileKey = "fileKey";
        businessData.put(fileKey, fileInfo);

        assertThrows(IllegalArgumentException.class, () -> DataUtils.getFilePath(businessData, fileKey));
    }

    @Test
    void testGetFilePathWithMissingKey() {
        Map<String, Object> businessData = new HashMap<>();

        assertThrows(NullPointerException.class, () -> {
            DataUtils.getFilePath(businessData, "nonExistentKey");
        });
    }

    @Test
    void testGetFilePathWithNullKey() {
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("file_path", null);
        String fileKey = "fileKey";
        businessData.put(fileKey, fileInfo);

        assertThrows(IllegalArgumentException.class, () -> {
            DataUtils.getFilePath(businessData, fileKey);
        });
    }

    @Test
    void testGetFirstFlowDataWithValidData() {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put("mockKey", "value");
        List<Map<String, Object>> flowDataList = new ArrayList<>();
        flowDataList.add(flowData);
        Map<String, Object> result = DataUtils.getFirstFlowData(flowDataList);

        assertNotNull(result);
        assertEquals(flowData, result);
    }

    @Test
    void testGetFirstFlowDataWithEmptyList() {
        List<Map<String, Object>> flowDataList = new ArrayList<>();

        assertThrows(JobberException.class, () -> DataUtils.getFirstFlowData(flowDataList));
    }

    @Test
    void testPutFromMapIfPresentWithKeyPresent() {
        Map<String, Object> from = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        String mockKey = "name";
        String value = "John Doe";
        from.put(mockKey, value);
        DataUtils.putFromMapIfPresent(from, mockKey, to);

        assertEquals(1, to.size());
        assertTrue(to.containsKey(mockKey));
        assertEquals(value, to.get(mockKey));
    }

    @Test
    void testPutFromMapIfPresentWithKeyNotPresent() {
        Map<String, Object> from = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        String mockKey = "name";
        DataUtils.putFromMapIfPresent(from, mockKey, to);

        assertEquals(0, to.size());
    }

    @Test
    void testPutFromMapIfPresentWithNullFrom() {
        Map<String, Object> from = null;
        Map<String, Object> to = new HashMap<>();
        String mockKey = "name";

        assertThrows(NullPointerException.class, () -> DataUtils.putFromMapIfPresent(from, mockKey, to));
    }

    @Test
    void testPutFromMapIfPresentWithNullTo() {
        Map<String, Object> from = new HashMap<>();
        from.put("name", "John Doe");
        Map<String, Object> to = null;
        String mockKey = "name";

        assertThrows(NullPointerException.class, () -> DataUtils.putFromMapIfPresent(from, mockKey, to));
    }

    @Test
    void testPutFromMapIfPresentWithKeyAlreadyInTo() {
        Map<String, Object> from = new HashMap<>();
        Map<String, Object> to = new HashMap<>();
        String mockKey = "name";
        String valueFrom = "John From";
        String valueTo = "Jane To";
        from.put(mockKey, valueFrom);
        to.put(mockKey, valueTo);
        DataUtils.putFromMapIfPresent(from, mockKey, to);

        assertEquals(1, to.size());
        assertEquals(valueFrom, to.get(mockKey));
    }

    @Test
    void testGetAppIdWithValidString() {
        Map<String, Object> businessData = new HashMap<>();
        String expectedAppId = "testAppId";
        businessData.put(AippConst.ATTR_APP_ID_KEY, expectedAppId);
        String appId = DataUtils.getAppId(businessData);

        assertEquals(expectedAppId, appId);
    }

    @Test
    void testGetAppIdWithEmptyMap() {
        Map<String, Object> businessData = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> DataUtils.getAppId(businessData));
    }

    @Test
    void testGetAppIdWithNonStringAppId() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.ATTR_APP_ID_KEY, 12345);

        assertThrows(IllegalArgumentException.class, () -> DataUtils.getAppId(businessData));
    }

    @Test
    void testGetAppIdWithNullValue() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.ATTR_APP_ID_KEY, null);

        assertThrows(IllegalArgumentException.class, () -> DataUtils.getAppId(businessData));
    }
}
