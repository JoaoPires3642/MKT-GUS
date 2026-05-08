import { fireEvent, render, screen } from "@testing-library/react"
import PaymentScreen from "@/components/payment-screen"

describe("PaymentScreen", () => {
  it("shows the available payment methods and starts payment", () => {
    const onConfirm = vi.fn()

    render(
      <PaymentScreen
        onConfirm={onConfirm}
        onBack={vi.fn()}
        appliedCoupon={null}
        totalAmount={19.9}
        paymentStatus={null}
        paymentError={null}
        isProcessingPayment={false}
      />,
    )

    fireEvent.click(screen.getByText("Cartao de Debito"))
    fireEvent.click(screen.getByRole("button", { name: "Iniciar Pagamento" }))

    expect(screen.getByText("Vale")).toBeInTheDocument()
    expect(onConfirm).toHaveBeenCalledWith("DEBIT")
  })

  it("shows processing feedback", () => {
    render(
      <PaymentScreen
        onConfirm={vi.fn()}
        onBack={vi.fn()}
        appliedCoupon={null}
        totalAmount={19.9}
        paymentStatus="PROCESSING"
        paymentError={null}
        isProcessingPayment={true}
      />,
    )

    expect(screen.getByText("Aguardando confirmacao do pagamento digital...")).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Processando..." })).toBeDisabled()
  })

  it("shows confirmed payment feedback", () => {
    render(
      <PaymentScreen
        onConfirm={vi.fn()}
        onBack={vi.fn()}
        appliedCoupon={null}
        totalAmount={19.9}
        paymentStatus="AUTHORIZED"
        paymentError={null}
        isProcessingPayment={false}
      />,
    )

    expect(screen.getByText("Pagamento confirmado. Finalizando compra...")).toBeInTheDocument()
  })

  it("shows generic failed payment feedback", () => {
    render(
      <PaymentScreen
        onConfirm={vi.fn()}
        onBack={vi.fn()}
        appliedCoupon={null}
        totalAmount={19.9}
        paymentStatus="FAILED"
        paymentError={null}
        isProcessingPayment={false}
      />,
    )

    expect(screen.getByText("O pagamento nao foi concluido. Tente novamente.")).toBeInTheDocument()
  })

  it("shows explicit backend error message when provided", () => {
    render(
      <PaymentScreen
        onConfirm={vi.fn()}
        onBack={vi.fn()}
        appliedCoupon={{ id: "1", name: "Cupom Teste", description: "desc", type: "fixed", value: 10, pointsCost: 20 }}
        totalAmount={19.9}
        paymentStatus={null}
        paymentError="Erro de comunicacao"
        isProcessingPayment={false}
      />,
    )

    expect(screen.getByText("Cupom aplicado: Cupom Teste")).toBeInTheDocument()
    expect(screen.getByText("Erro de comunicacao")).toBeInTheDocument()
  })

  it("shows a dedicated canceled payment message", () => {
    render(
      <PaymentScreen
        onConfirm={vi.fn()}
        onBack={vi.fn()}
        appliedCoupon={null}
        totalAmount={19.9}
        paymentStatus="CANCELED"
        paymentError={null}
        isProcessingPayment={false}
      />,
    )

    expect(screen.getByText("Pagamento cancelado. Escolha outra forma de pagamento ou tente novamente.")).toBeInTheDocument()
  })
})
