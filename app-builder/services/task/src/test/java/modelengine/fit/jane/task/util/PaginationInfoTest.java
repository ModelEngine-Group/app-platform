/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 功能描述
 *
 * @author 梁济时
 * @since 2023-12-12
 */
@DisplayName("测试分页相关类")
class PaginationInfoTest {
    private static final long OFFSET = 20L;
    private static final int LIMIT = 80;
    private static final long TOTAL = 120L;
    private static final List<Integer> RESULTS = Arrays.asList(100, 200);

    @Nested
    @DisplayName("分页信息测试")
    class PaginationTest {
        @Test
        @DisplayName("分页信息中包含正确的信息")
        void should_return_with_correct_data() {
            Pagination pagination = Pagination.create(OFFSET, LIMIT);
            assertEquals(OFFSET, pagination.offset());
            assertEquals(LIMIT, pagination.limit());
        }

        @Test
        @DisplayName("分页信息中包含相同的信息时，equals 返回 true")
        void should_return_true_when_contains_same_data() {
            Pagination pagination1 = Pagination.create(OFFSET, LIMIT);
            Pagination pagination2 = Pagination.create(OFFSET, LIMIT);
            assertNotSame(pagination1, pagination2);
            assertEquals(pagination1, pagination2);
        }

        @Test
        @DisplayName("分页信息中包含相同信息时，hashCode 返回相同的值")
        void should_return_same_hash_when_contains_same_data() {
            Pagination pagination1 = Pagination.create(OFFSET, LIMIT);
            Pagination pagination2 = Pagination.create(OFFSET, LIMIT);
            assertEquals(pagination1.hashCode(), pagination2.hashCode());
        }

        @Test
        @DisplayName("返回的字符串中包含友好的信息")
        void should_return_friendly_string() {
            Pagination pagination = Pagination.create(OFFSET, LIMIT);
            String actual = pagination.toString();
            String expected = "[offset=20, limit=80]";
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("分页结果信息测试")
    class PaginationResultTest {
        @Test
        @DisplayName("分页信息中包含正确的信息")
        void should_return_with_correct_data() {
            PaginationResult pagination = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            assertEquals(OFFSET, pagination.offset());
            assertEquals(LIMIT, pagination.limit());
            assertEquals(TOTAL, pagination.total());
        }

        @Test
        @DisplayName("分页信息中包含相同的信息时，equals 返回 true")
        void should_return_true_when_contains_same_data() {
            PaginationResult pagination1 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PaginationResult pagination2 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            assertNotSame(pagination1, pagination2);
            assertEquals(pagination1, pagination2);
        }

        @Test
        @DisplayName("分页信息中包含相同信息时，hashCode 返回相同的值")
        void should_return_same_hash_when_contains_same_data() {
            PaginationResult pagination1 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PaginationResult pagination2 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            assertEquals(pagination1.hashCode(), pagination2.hashCode());
        }

        @Test
        @DisplayName("返回的字符串中包含友好的信息")
        void should_return_friendly_string() {
            PaginationResult pagination = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            String actual = pagination.toString();
            String expected = "[offset=20, limit=80, total=120]";
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("分页结果集信息测试")
    class PagedResultSetTest {
        @Test
        @DisplayName("分页信息中包含正确的信息")
        void should_return_with_correct_data() {
            PaginationResult pagination = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result = PagedResultSet.create(RESULTS, pagination);
            assertEquals(OFFSET, result.pagination().offset());
            assertEquals(LIMIT, result.pagination().limit());
            assertEquals(TOTAL, result.pagination().total());
            assertIterableEquals(RESULTS, result.results());
        }

        @Test
        @DisplayName("分页信息中包含相同的信息时，equals 返回 true")
        void should_return_true_when_contains_same_data() {
            PaginationResult pagination1 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result1 = PagedResultSet.create(RESULTS, pagination1);
            PaginationResult pagination2 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result2 = PagedResultSet.create(RESULTS, pagination2);
            assertNotSame(result1, result2);
            assertEquals(result1, result2);
        }

        @Test
        @DisplayName("分页信息中包含相同信息时，hashCode 返回相同的值")
        void should_return_same_hash_when_contains_same_data() {
            PaginationResult pagination1 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result1 = PagedResultSet.create(RESULTS, pagination1);
            PaginationResult pagination2 = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result2 = PagedResultSet.create(RESULTS, pagination2);
            assertEquals(result1.hashCode(), result2.hashCode());
        }

        @Test
        @DisplayName("返回的字符串中包含友好的信息")
        void should_return_friendly_string() {
            PaginationResult pagination = PaginationResult.create(OFFSET, LIMIT, TOTAL);
            PagedResultSet<Integer> result = PagedResultSet.create(RESULTS, pagination);
            String actual = result.toString();
            String expected = "[pagination=[offset=20, limit=80, total=120], results=[100, 200]]";
            assertEquals(expected, actual);
        }
    }
}
