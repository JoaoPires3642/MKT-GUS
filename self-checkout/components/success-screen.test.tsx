import { fireEvent, render, screen } from "@testing-library/react"
import SuccessScreen from "@/components/success-screen"
import type { ConfirmPurchaseResult } from "@/lib/types"

const order: ConfirmPurchaseResult = {
  id: 123,
  customerCpf: 12345678901,
  couponId: null,
  orderedAt: "2026-05-08T12:30:00",
  totalAmount: 24.5,
  updatedPointsBalance: 180,
  taxDocument: {
    status: "ISSUED",
    numeroDocumento: "NF-123",
    chaveAcesso: "35123456789012345678901234567890123456789012",
    urlDanfe: "https://danfe.local/documento/123",
    motivoFalha: null,
  },
  items: [
    {
      ean: "7891000000001",
      productName: "Agua Mineral",
      unitPrice: 4.5,
      quantity: 2,
      adultOnly: false,
      totalPrice: 9,
    },
    {
      ean: "7891000000002",
      productName: "Salgadinho",
      unitPrice: 15.5,
      quantity: 1,
      adultOnly: false,
      totalPrice: 15.5,
    },
  ],
}

describe("SuccessScreen", () => {
  afterEach(() => {
    vi.useRealTimers()
  })

  it("shows fiscal print data including payment method", () => {
    render(<SuccessScreen order={order} paymentMethod="DEBIT" onNewPurchase={vi.fn()} />)

    expect(screen.getByText("Deseja imprimir a nota fiscal?")).toBeInTheDocument()
    expect(screen.getAllByText("DANFE Simplificada").length).toBeGreaterThanOrEqual(1)
    expect(screen.getByText("Pedido:")).toBeInTheDocument()
    expect(screen.getByText("Agua Mineral")).toBeInTheDocument()
    expect(screen.getByText("Cartao de debito")).toBeInTheDocument()
  })

  it("shows coupon discount in fiscal print data", () => {
    render(<SuccessScreen order={{ ...order, couponId: 1, totalAmount: 20 }} paymentMethod="PIX" onNewPurchase={vi.fn()} />)

    expect(screen.getByText("Subtotal")).toBeInTheDocument()
    expect(screen.getByText("Desconto de cupom")).toBeInTheDocument()
    expect(screen.getByText("- R$ 4,50")).toBeInTheDocument()
    expect(screen.getByText("R$ 20,00")).toBeInTheDocument()
  })

  it("starts a new purchase when requested", () => {
    const onNewPurchase = vi.fn()

    render(<SuccessScreen order={order} paymentMethod="PIX" onNewPurchase={onNewPurchase} />)

    fireEvent.click(screen.getByRole("button", { name: "Nova Compra" }))

    expect(onNewPurchase).toHaveBeenCalledTimes(1)
  })

  it("returns automatically after timeout", () => {
    vi.useFakeTimers()
    const onNewPurchase = vi.fn()

    render(<SuccessScreen order={order} paymentMethod="PIX" onNewPurchase={onNewPurchase} />)

    vi.advanceTimersByTime(15000)

    expect(onNewPurchase).toHaveBeenCalledTimes(1)
  })
})
