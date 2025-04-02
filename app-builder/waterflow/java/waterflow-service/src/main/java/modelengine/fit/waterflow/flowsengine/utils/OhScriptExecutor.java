/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.utils;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.jade.waterflow.ErrorCodes;
import modelengine.fit.jade.waterflow.exceptions.WaterflowException;
import modelengine.fit.ohscript.script.errors.GrammarSyntaxException;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.parser.AST;
import modelengine.fit.ohscript.script.parser.ParserBuilder;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.common.utils.UUIDUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OhScript执行类
 *
 * @author 李哲峰
 * @since 2023/12/21
 */
public final class OhScriptExecutor {
    /**
     * OhScript前段脚本用于获取条件节点用户完整数据
     * 最终的用户数据变量名称需要与客户约定
     */
    private static final String EXTERNAL_PARAMETER_KEY = "userData";

    private static final String CODE_SEGMENT_PREFIX = "";

    private static final Logger log = Logger.get(OhScriptExecutor.class);

    private static final ThreadLocal<Map<String, AST>> AST_CACHE = new ThreadLocal<>();

    /**
     * 执行用户给定的OhScript条件判定脚本，返回判定结果
     *
     * @param flowData {@link FlowData} 流程执行过程中的用户数据
     * @param conditionRule 需要进行判断的条件表达式
     * @return 条件表达式的判定结果
     */
    public static boolean evaluateConditionRule(FlowData flowData, String conditionRule) {
        ParserBuilder parserBuilder = new ParserBuilder();
        JSONObject businessData = new JSONObject(
                Optional.ofNullable(flowData.getBusinessData()).orElse(new JSONObject()));
        String formatConditionRule = FlowUtil.formatConditionRule(conditionRule).trim();
        // 兼容历史 !{{var}}类型，且传入的是字符串类型的true/false
        boolean isPatternOfNot = false;
        if (FlowUtil.isPatternOfNot(conditionRule)) {
            isPatternOfNot = true;
            formatConditionRule = formatConditionRule.substring(1);
        }
        String ohScript = CODE_SEGMENT_PREFIX + formatConditionRule;
        String uuid = UUIDUtil.fastUuid();
        log.warn("uuid:{0}, evaluateConditionRule:{1}", uuid, ohScript);
        Object execResult = getExecResult(conditionRule, parserBuilder, ohScript, businessData);
        log.warn("uuid:{0}, execResult:{1}", uuid, execResult);
        if (execResult instanceof String) {
            if ("TRUE".equalsIgnoreCase(ObjectUtils.cast(execResult))) {
                execResult = true;
            }
            if ("FALSE".equalsIgnoreCase(ObjectUtils.cast(execResult))) {
                execResult = false;
            }
        }
        if (!(execResult instanceof Boolean)) {
            String exceptionMsg = String.format("Unexpected FlowConditionNode OhScript return value. "
                    + "OhScript Content: \"%s\"; Return Value: %s", ohScript, execResult);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}", exceptionMsg);
            throw new WaterflowException(ErrorCodes.TYPE_CONVERT_FAILED);
        }
        boolean result = ObjectUtils.cast(execResult);
        return isPatternOfNot ? !result : result;
    }

    private static Object getExecResult(String conditionRule, ParserBuilder parserBuilder, String code,
            Map<String, Object> businessData) {
        Object execResult;
        try {
            AST ast = buildAst(parserBuilder, code, businessData);
            ASTEnv env = new ASTEnv(ast);
            env.grant(Constant.BUSINESS_DATA_KEY, businessData);
            execResult = env.execute();
        } catch (GrammarSyntaxException e) {
            String exceptionMsg = StringUtils.format("Invalid OhScript input. OhScript Content: \"{0}\"", code);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}, error cause: {}",
                    exceptionMsg, e.getMessage());
            throw new WaterflowException(ErrorCodes.FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR, code);
        } catch (OhPanic | ScriptExecutionException | NullPointerException e) {
            String exceptionMsg = StringUtils.format("Condition rule cannot be evaluated. Condition Rule: \"{0}\"",
                    conditionRule);
            log.error("The FlowConditionNode failed to judge the flow condition, error message: {}, error cause: {}",
                    exceptionMsg, e.getMessage());
            throw new WaterflowException(ErrorCodes.FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR, conditionRule);
        }
        return execResult;
    }

    private static AST buildAst(ParserBuilder parserBuilder, String code, Map<String, Object> businessData) {
        Map<String, AST> astMap = AST_CACHE.get();
        if (astMap == null) {
            astMap = new HashMap<>();
            AST_CACHE.set(astMap);
        }
        AST ast = astMap.get(code);
        if (ast == null) {
            parserBuilder.addExternalOh(Constant.BUSINESS_DATA_KEY, businessData);
            ast = parserBuilder.parseString("", code);
            astMap.put(code, ast);
        }
        return ast;
    }
}
