/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import Icon, { UploadOutlined } from '@ant-design/icons';
import React from 'react';
import { BaseIcons } from '../components/icons/base';

// 编辑按钮
const LeftArrowIcon = (props) => <Icon component={() => (<BaseIcons.LeftArrow />)} {...props} />;
const StartIcon = (props) => <Icon component={() => (<BaseIcons.Start />)} {...props} />;
const EndIcon = (props) => <Icon component={() => (<BaseIcons.End />)} {...props} />;
const LlmIcon = (props) => <Icon component={() => (<BaseIcons.LLM />)} {...props} />;
const ManualCheckIcon = (props) => <Icon component={() => (<BaseIcons.ManualCheck />)} {...props} />;
const IfIcon = (props) => <Icon component={() => (<BaseIcons.If />)} {...props} />;
const FitIcon = (props) => <Icon component={() => (<BaseIcons.Fit />)} {...props} />;
const DataRetrievalIcon = (props) => <Icon component={() => (<BaseIcons.DataRetrieval />)} {...props} />;
const UploadIcon = (props) => <UploadOutlined {...props} />;
const ConfigFlowIcon = (props) => <Icon component={() => (<BaseIcons.ConfigFlow />)} {...props} />;
const DownLoadIcon = (props) => <Icon component={() => (<BaseIcons.DownLoad />)} {...props} />;
const FullScreenIcon = (props) => <Icon component={() => (<BaseIcons.FullScreen />)} {...props} />;
const LinkIcon = (props) => <Icon component={() => (<BaseIcons.Link />)} {...props} />;
const AtIcon = (props) => <Icon component={() => (<BaseIcons.At />)} {...props} />;
const PanleIcon = (props) => <Icon component={() => (<BaseIcons.Panle />)} {...props} />;
const PanleCloseIcon = (props) => <Icon component={() => (<BaseIcons.PanleClose />)} {...props} />;
const AppBoxIcon = (props) => <Icon component={() => (<BaseIcons.AppBox />)} {...props} />;
const CreateAppIcon = (props) => <Icon component={() => (<BaseIcons.CreateApp />)} {...props} />;
const AudioIcon = (props) => <Icon component={() => (<BaseIcons.Audio />)} {...props} />;
const AudioActiveIcon = (props) => <Icon component={() => (<BaseIcons.AudioActive />)} {...props} />;
const CloseIcon = (props) => <Icon component={() => (<BaseIcons.Close />)} {...props} />;
const RunIcon = (props) => <Icon component={() => (<BaseIcons.Run />)} {...props} />;
const HistoryIcon = (props) => <Icon component={() => (<BaseIcons.History />)} {...props} />;
const RebotIcon = (props) => <Icon component={() => (<BaseIcons.Rebot />)} {...props} />;
const LikeIcon = (props) => <Icon component={() => (<BaseIcons.Like />)} {...props} />;
const UnlikeIcon = (props) => <Icon component={() => (<BaseIcons.Unlike />)} {...props} />;
const LikeSelectIcon = (props) => <Icon component={() => (<BaseIcons.LikeSelect />)} {...props} />;
const UnlikeSelectIcon = (props) => <Icon component={() => (<BaseIcons.UnlikeSelect />)} {...props} />;
const ShareIcon = (props) => <Icon component={() => (<BaseIcons.Share />)} {...props} />;
const CopyIcon = (props) => <Icon component={() => (<BaseIcons.Copy />)} {...props} />;
const CopyUrlIcon = (props) => <Icon component={() => (<BaseIcons.CopyUrl />)} {...props} />;
const DeleteIcon = (props) => <Icon component={() => (<BaseIcons.Delete />)} {...props} />;
const DeleteContentIcon = (props) => <Icon component={() => (<BaseIcons.DeleteContent />)} {...props} />;
const ClearFileIcon = (props) => <Icon component={() => (<BaseIcons.ClearFile />)} {...props} />;
const NewFeatIcon = (props) => <Icon component={() => (<BaseIcons.NewFeat />)} {...props} />;
const RocketIcon = (props) => <Icon component={() => (<BaseIcons.Rocket />)} {...props} />;
const FixIcon = (props) => <Icon component={() => (<BaseIcons.Fix />)} {...props} />;
const CodeIcon = (props) => <Icon component={() => (<BaseIcons.Code />)} {...props} />;
const ChatUserIcon = (props) => <Icon component={() => (<BaseIcons.ChatUser />)} {...props} />;
const PlayIcon = (props) => <Icon component={() => (<BaseIcons.Play />)} {...props} />;
const StopIcon = (props) => <Icon component={() => (<BaseIcons.Stop />)} {...props} />;
const NotificationIcon = (props) => <Icon component={() => (<BaseIcons.Notification />)} {...props} />;
const ArrowDownIcon = (props) => <Icon component={() => (<BaseIcons.ArrowDown />)} {...props} />;
const ClassificationIcon = (props) => <Icon component={() => (<BaseIcons.Classification />)} {...props} />;
const QueryOptimizationIcon = (props) => <Icon component={() => (<BaseIcons.QueryOptimization />)} {...props} />;
const KnowledgeRetrievalIcon = (props) => <Icon component={() => (<BaseIcons.KnowledgeRetrieval />)} {...props} />;
const TextExtractionIcon = (props) => <Icon component={() => (<BaseIcons.TextExtraction />)} {...props} />;
const InspirationIcon = (props) => <Icon component={() => (<BaseIcons.Inspiration />)} {...props} />;
const SendIcon = (props) => <Icon component={() => (<BaseIcons.Send />)} {...props} />;
const ConfigurationIcon = (props) => <Icon component={() => (<BaseIcons.Configuration />)} {...props} />;
const HttpIcon = (props) => <Icon component={() => (<BaseIcons.Http />)} {...props} />;
const VariableAggregation = (props) => <Icon component={() => (<BaseIcons.VariableAggregation />)} {...props} />;
const TextToImageIcon = (props) => <Icon component={() => (<BaseIcons.TextToImage />)} {...props} />;
const FileExtractionIcon = (props) => <Icon component={() => (<BaseIcons.FileExtraction />)} {...props} />;
const LoopIcon = (props) => <Icon component={() => (<BaseIcons.Loop />)} {...props} />;
const PairingIcon = (props) => <Icon component={() => (<BaseIcons.Pairing />)} {...props} />;
const TextConcatenateIcon = (props) => <BaseIcons.TextConcatenate {...props} />;

export {
  LeftArrowIcon,
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ManualCheckIcon,
  LlmIcon,
  UploadIcon,
  ConfigFlowIcon,
  DownLoadIcon,
  FullScreenIcon,
  IfIcon,
  FitIcon,
  LinkIcon,
  AtIcon,
  PanleIcon,
  PanleCloseIcon,
  AppBoxIcon,
  CreateAppIcon,
  AudioIcon,
  AudioActiveIcon,
  HistoryIcon,
  RebotIcon,
  LikeIcon,
  UnlikeIcon,
  LikeSelectIcon,
  UnlikeSelectIcon,
  CloseIcon,
  RunIcon,
  ShareIcon,
  CopyIcon,
  DeleteIcon,
  DeleteContentIcon,
  ClearFileIcon,
  NewFeatIcon,
  RocketIcon,
  FixIcon,
  CodeIcon,
  ChatUserIcon,
  PlayIcon,
  StopIcon,
  NotificationIcon,
  ArrowDownIcon,
  ClassificationIcon,
  QueryOptimizationIcon,
  KnowledgeRetrievalIcon,
  TextExtractionIcon,
  CopyUrlIcon,
  InspirationIcon,
  SendIcon,
  ConfigurationIcon,
  HttpIcon,
  VariableAggregation,
  TextToImageIcon,
  FileExtractionIcon,
  LoopIcon,
  PairingIcon,
  TextConcatenateIcon
}

