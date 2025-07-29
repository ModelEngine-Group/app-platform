
export enum TaskStatusE {
  PUBLISHED = 'PUBLISHED',
  UNPUBLISHED = 'UNPUBLISHED',
  DELETED = 'DELETED',
}

export interface getEvalTaskListParamsI {
  createTimeTo: string;
  author: string;
  createTimeFrom: string;
  appId: string;
  pageSize: number;
  finish: [];
  pageIndex: number;
  version: string;
}

export interface evalTaskI {
  createTime: string;
  author: string;
  passRate: number;
  finish: boolean;
  datasets: Array<string>;
  id: number;
  version: string;
}

export interface traceI {
  output: string;
  input: string;
  latency: number;
  time: string;
  nodeId: string;
}

export interface evalreportI {
  output: string;
  score: number;
  input: string;
  trace: Array<traceI>;
  meta: string;
  latency: number;
  expectedOutput: string;
  id: number;
}

export enum RunStatus {
  RUNNING = 'RUNNING',
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
}
