export const PRICE_OVERRIDE_REASONS = [
  { value: "ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA", label: "Etiqueta promocional nao atualizada" },
  { value: "PROMOCAO_NAO_SINCRONIZADA", label: "Promocao nao sincronizada" },
  { value: "ETIQUETA_DESATUALIZADA", label: "Etiqueta desatualizada" },
  { value: "AJUSTE_OPERACIONAL", label: "Ajuste operacional" },
] as const

export function getPriceOverrideReasonLabel(reason: string) {
  return PRICE_OVERRIDE_REASONS.find((option) => option.value === reason)?.label ?? reason
}
