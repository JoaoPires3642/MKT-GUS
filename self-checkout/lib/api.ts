import axios from "axios"
import type { Coupon, PriceOverride, Product } from "@/lib/types"

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

export async function fetchCustomerPoints(cpf: string) {
  const response = await fetch(`${API_BASE_URL}/pessoa/verificar-cpf`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ cpf }),
  })

  if (!response.ok) {
    throw new Error("Erro ao verificar CPF")
  }

  return response.json()
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
}) {
  const { appliedCoupon, cart, cpf, hasCpf } = params
  const payload = {
    clienteCpf: hasCpf && cpf ? cpf.replace(/\D/g, "") : null,
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

  return response.json()
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
