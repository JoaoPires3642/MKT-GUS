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
  it("shows printing message without displaying the tax document", () => {
    render(<SuccessScreen order={order} onNewPurchase={vi.fn()} />)

    expect(screen.getByText("Compra Finalizada")).toBeInTheDocument()
    expect(screen.getByText("Imprimindo nota fiscal...")).toBeInTheDocument()
    expect(screen.queryByText("Pedido #123 confirmado com sucesso.")).not.toBeInTheDocument()
    expect(screen.queryByText("Nota Fiscal")).not.toBeInTheDocument()
    expect(screen.queryByText("Agua Mineral")).not.toBeInTheDocument()
  })

  it("starts a new purchase when requested", () => {
    const onNewPurchase = vi.fn()

    render(<SuccessScreen order={order} onNewPurchase={onNewPurchase} />)

    fireEvent.click(screen.getByRole("button", { name: "Nova Compra" }))

    expect(onNewPurchase).toHaveBeenCalledTimes(1)
  })
})
