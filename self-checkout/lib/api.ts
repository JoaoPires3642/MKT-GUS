import axios from "axios"
import type { ConfirmPurchaseResult, Coupon, PaymentMethod, PaymentTransactionResult, PriceOverride, Product } from "@/lib/types"

const API_BASE_URL = "http://localhost:8080"

type RawProduct = {
  adultOnly?: boolean
  ean?: string
  imageUrl?: string
  message?: string
  name?: string
  nome?: string
  price?: number
  produtoMaiorDeIdade?: boolean
  urlImagem?: string
  valor?: number
}

type CustomerPointsResponse = number | { pontos?: number }

export async function fetchCustomerPoints(cpf: string): Promise<CustomerPointsResponse> {
  const response = await fetch(`${API_BASE_URL}/pessoa/verificar-cpf`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ cpf }),
  })

  if (!response.ok) {
    throw new Error("Erro ao verificar CPF")
  }

  return (await response.json()) as CustomerPointsResponse
}

export async function verifyEmployeeRegistration(registration: string) {
  const response = await axios.post(`${API_BASE_URL}/api/funcionarios/verificar-matricula`, {
    matricula: registration,
  })
  return response.data as { message?: string; valid?: boolean }
}

export async function fetchProductByBarcode(barcode: string) {
  const response = await axios.post(`${API_BASE_URL}/produtos/buscar`, { barcode })
  return response.data as RawProduct
}

export async function fetchCoupons() {
  const response = await fetch(`${API_BASE_URL}/api/cupons`)
  if (!response.ok) {
    throw new Error("Erro ao buscar cupons")
  }

  const data = await response.json()
  return data as Array<Record<string, unknown>>
}

export async function confirmPurchase(params: {
  appliedCoupon: Coupon | null
  cart: Product[]
  cpf: string
  hasCpf: boolean
  paymentTransactionId: number
}): Promise<ConfirmPurchaseResult> {
  const { appliedCoupon, cart, cpf, hasCpf, paymentTransactionId } = params
  const payload = {
    clienteCpf: hasCpf && cpf ? cpf.replace(/\D/g, "") : null,
    paymentTransactionId,
    itens: cart.map((item) => ({
      ajustePreco: mapPriceOverride(item.priceOverride),
      ean: item.ean,
      quantidade: item.quantity,
      valorUnitario: item.price,
    })),
    cupom: appliedCoupon
      ? {
          desconto: appliedCoupon.value,
          id: Number.parseInt(appliedCoupon.id, 10),
          tipoDesconto: appliedCoupon.type,
        }
      : null,
  }

  const response = await fetch(`${API_BASE_URL}/pedidos/confirmar-compra`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => null)
    throw new Error(errorData?.message ?? "Erro ao confirmar o pedido")
  }

  return (await response.json()) as ConfirmPurchaseResult
}

export async function startPayment(params: { amount: number; method: PaymentMethod }) {
  const response = await fetch(`${API_BASE_URL}/pagamentos/iniciar`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(params),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => null)
    throw new Error(errorData?.message ?? "Erro ao iniciar pagamento")
  }

  return (await response.json()) as PaymentTransactionResult
}

export async function fetchPaymentStatus(paymentId: number) {
  const response = await fetch(`${API_BASE_URL}/pagamentos/${paymentId}`)

  if (!response.ok) {
    const errorData = await response.json().catch(() => null)
    throw new Error(errorData?.message ?? "Erro ao consultar pagamento")
  }

  return (await response.json()) as PaymentTransactionResult
}

export function mapBackendProduct(data: RawProduct, fallback?: Partial<Product>): Product | null {
  if (!data.ean) {
    return null
  }

  return {
    ean: data.ean,
    id: Date.now(),
    image: data.imageUrl ?? data.urlImagem ?? fallback?.image,
    isAdult: data.adultOnly ?? data.produtoMaiorDeIdade ?? fallback?.isAdult,
    name: data.name ?? data.nome ?? fallback?.name ?? "Produto",
    price: data.price ?? data.valor ?? fallback?.price ?? 0,
    quantity: fallback?.quantity ?? 1,
  }
}

function mapPriceOverride(priceOverride?: PriceOverride) {
  if (!priceOverride) {
    return null
  }

  return {
    matriculaFuncionario: priceOverride.employeeRegistration,
    motivo: priceOverride.reason,
    valorAutorizado: priceOverride.authorizedUnitPrice,
  }
}
