export interface Product {
  id: number
  name: string
  description?: string
  price: number
  originalPrice?: number
  quantity: number
  image?: string
  ean: string
  isAdult?: boolean
  priceOverride?: PriceOverride
}

export interface PriceOverride {
  employeeRegistration: string
  authorizedUnitPrice: number
  reason: string
}

export interface Coupon {
  id: string
  name: string
  description: string
  type: "percentage" | "fixed"
  value: number
  pointsCost: number
  //discount: number; // Valor do desconto em reais ou percentual
  minPurchase?: number
  maxDiscount?: number
}

export interface OrderItemSummary {
  ean: string
  productName: string
  unitPrice: number
  quantity: number
  adultOnly: boolean
  totalPrice: number
}

export interface TaxDocumentSummary {
  status: string
  numeroDocumento: string | null
  chaveAcesso: string | null
  urlDanfe: string | null
  motivoFalha: string | null
}

export interface ConfirmPurchaseResult {
  id: number
  customerCpf: number | null
  couponId: number | null
  orderedAt: string
  totalAmount: number
  items: OrderItemSummary[]
  updatedPointsBalance: number | null
  taxDocument: TaxDocumentSummary | null
}

export type PaymentMethod = "CREDIT" | "DEBIT" | "VALE" | "PIX"

export type PaymentStatus = "PENDING" | "PROCESSING" | "AUTHORIZED" | "PAID" | "FAILED" | "CANCELED" | "EXPIRED"

export interface PaymentTransactionResult {
  id: number
  provider: string
  providerReference: string
  method: PaymentMethod
  status: PaymentStatus
  amount: number
  failureReason: string | null
  expiresAt: string | null
  confirmedAt: string | null
}
