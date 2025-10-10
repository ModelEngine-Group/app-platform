# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
import json
import multiprocessing
import threading
import unittest

try:
    from python_repl_impl import execute_node_impl, GLOBAL_CONFIG
except ImportError as e:
    from .python_repl_impl import execute_node_impl, GLOBAL_CONFIG

WITH_ARGS_CODE = """
async def main(args):
    return args['n']
"""

WITHOUT_ARGS_CODE = """
async def main():
    return args['n']
"""

INVALID_MODULE_CODE = """
import time
def main(args):
    return 1
"""

NO_ENTRYPOINT_CODE = """
async def mine(args):
    return 1
"""

SYNTAX_ERROR_CODE = """
async def main(args):
    return 1////2
"""

ASYNC_CODE = """
import asyncio
async def main(args):
    await asyncio.sleep(10)
    return 1
"""

INF_LOOP_CODE = """
async def main(args):
    while True:
        continue
    return 1
"""

NON_ASYNC_CODE = """
def main(args):
    return 1
"""

WARN_CODE = '''
async def exception_frame():
    try:
        import os
    except Exception as e:
        return e.__traceback__.tb_frame.f_back.f_back.f_globals['_IMPORT_WHITELIST']
b=exception_frame()
b.add('os')
'''

WARN_CODE1 = '''
async def exception_frame():
    ret = ().__class__.__bases__[0].__subclasses__()[133].__globals__["mkdir"]("tmp_file")
'''

WARN_CODE2 = '''
async def exception_frame():
    try:
        import os
    except Exception as e:
        return e.tb_frame.f_back.f_back.f_globals['_IMPORT_WHITELIST']
b=exception_frame()
b.add('os')
'''

ERROR_CODE = """
async def main(args):
    return 1/0
"""

UNSERIALIZABLE_CODE = """
async def main(args):
    return {"n" : 1}.keys() 
"""

MEMORY_ERROR_CODE = """
async def main(args):
    k =  [i for i in range(10**7)]
    return k
"""


class CodeExecutor:
    def __init__(self):
        self.pools = []
        for _ in range(GLOBAL_CONFIG["max_pool"]):
            lock = threading.Lock()
            pool = multiprocessing.Pool(processes=1)
            self.pools.append((lock, pool))
        self.index = 0
        self.index_lock = threading.Lock()
        self.config = GLOBAL_CONFIG

    def get_and_increment(self) -> int:
        with self.index_lock:
            i = self.index
            self.index = i + 1 if i < self.config["max_pool"] - 1 else 0
            return i


executor = CodeExecutor()
user_args = dict()


class TestStringMethods(unittest.TestCase):
    def test_with_args(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), {"n": 1}, WITH_ARGS_CODE, executor.config)
        self.assertTrue(res.isOk)
        self.assertEqual(json.loads(res.value), 1)

    def test_without_args(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), {"n": 1}, WITHOUT_ARGS_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, "[TypeError] main() takes 0 positional arguments but 1 was given")

    def test_key_not_exist(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, WITH_ARGS_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, "[KeyError] key 'n' do not exist")

    def test_invalid_module(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, INVALID_MODULE_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[NameError] model time is not valid')

    def test_no_entrypoint(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, NO_ENTRYPOINT_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[NameError] main function not defined')

    def test_syntax_error(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, SYNTAX_ERROR_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[SyntaxError] invalid syntax at line 2, column 15:     return 1////2')

    def test_async_timeout(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, ASYNC_CODE, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[TimeoutError] Execution timed out')

    def test_non_async_code(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, NON_ASYNC_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, "Unable to execute non-asynchronous function")

    def test_warn_code(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, WARN_CODE, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '__traceback__ is not allowed in code node')

    def test_warn_code_double_under_score(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, WARN_CODE1, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '__class__ is not allowed in code node')

    def test_warn_code_frame_escape(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, WARN_CODE2, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '.tb_frame is not allowed in code node')

    def test_code_error(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, ERROR_CODE, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[ZeroDivisionError] division by zero')

    def test_unserializable_code(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), user_args, UNSERIALIZABLE_CODE,
                                executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, "[TypeError] Object of type dict_keys is not JSON serializable")

    def test_inf_loop(self):
        res = execute_node_impl(executor.pools, executor.get_and_increment(), {"n": 1}, INF_LOOP_CODE, executor.config)
        self.assertFalse(res.isOk)
        self.assertEqual(res.msg, '[TimeoutError] Execution timed out')
