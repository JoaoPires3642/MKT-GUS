export interface Product {
  id: number
  name: string
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
