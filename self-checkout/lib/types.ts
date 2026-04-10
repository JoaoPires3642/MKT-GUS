export interface Product {
  id: number
  name: string
  price: number
  quantity: number
  image?: string
  ean: string
  isAdult?: boolean
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
