import i18n from "@/locale/i18n";

export const ERROR_CODES:any = {
  400: i18n.t('codeErrorRequest'),
  401: i18n.t('codeErrorUnauthorized'),
  403: i18n.t('codeErrorAccess'),
  404: i18n.t('codeErrorNotExist'),
  500: i18n.t('codeErrorInternal'),
  502: i18n.t('codeErrorGateway'),
  503: i18n.t('codeErrorBusy'),
  504: i18n.t('codeErrorTimeout'),
}
