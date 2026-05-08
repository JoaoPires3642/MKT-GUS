import { act, renderHook, waitFor } from "@testing-library/react"
import { useSelfCheckout } from "@/hooks/use-self-checkout"
import type { ConfirmPurchaseResult, PaymentTransactionResult } from "@/lib/types"

vi.mock("@/lib/api", () => ({
  confirmPurchase: vi.fn(),
  fetchCustomerPoints: vi.fn(),
  fetchPaymentStatus: vi.fn(),
  fetchProductByBarcode: vi.fn(),
  mapBackendProduct: vi.fn(),
  startPayment: vi.fn(),
  verifyEmployeeRegistration: vi.fn(),
}))

const api = await import("@/lib/api")

const paymentProcessing: PaymentTransactionResult = {
  id: 1,
  provider: "fake",
  providerReference: "fake-ref",
  method: "PIX",
  status: "PROCESSING",
  amount: 19.9,
  failureReason: null,
  expiresAt: "2026-05-08T18:00:00",
  confirmedAt: null,
}

const paymentPaid: PaymentTransactionResult = {
  ...paymentProcessing,
  status: "PAID",
  confirmedAt: "2026-05-08T18:00:02",
}

const confirmPurchaseResult: ConfirmPurchaseResult = {
  id: 10,
  customerCpf: null,
  couponId: null,
  orderedAt: "2026-05-08T18:00:03",
  totalAmount: 19.9,
  items: [],
  updatedPointsBalance: null,
  taxDocument: null,
}

describe("useSelfCheckout payment flow", () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it("completes purchase after successful payment polling", async () => {
    vi.mocked(api.startPayment).mockResolvedValue(paymentProcessing)
    vi.mocked(api.fetchPaymentStatus).mockResolvedValue(paymentPaid)
    vi.mocked(api.confirmPurchase).mockResolvedValue(confirmPurchaseResult)

    const { result } = renderHook(() => useSelfCheckout())

    act(() => {
      result.current.actions.addProduct({ ean: "999", id: 1, name: "Produto", price: 19.9, quantity: 1 })
    })

    await act(async () => {
      await result.current.actions.handlePaymentConfirm("PIX")
    })

    await waitFor(() => expect(result.current.state.currentScreen).toBe("success"))
    expect(result.current.state.completedPurchase?.id).toBe(10)
  })

  it("shows provider failure returned by payment status", async () => {
    vi.useFakeTimers()
    vi.mocked(api.startPayment).mockResolvedValue(paymentProcessing)
    vi.mocked(api.fetchPaymentStatus).mockResolvedValue({
      ...paymentProcessing,
      status: "FAILED",
      failureReason: "Pagamento recusado pela operadora.",
    })

    const { result } = renderHook(() => useSelfCheckout())

    act(() => {
      result.current.actions.addProduct({ ean: "999", id: 1, name: "Produto", price: 19.9, quantity: 1 })
    })

    await act(async () => {
      const promise = result.current.actions.handlePaymentConfirm("PIX")
      await vi.runAllTimersAsync()
      await promise
    })

    expect(result.current.state.paymentError).toBe("Pagamento recusado pela operadora.")
    expect(result.current.state.currentScreen).toBe("welcome")
  })

  it("shows communication error when start payment fails", async () => {
    vi.mocked(api.startPayment).mockRejectedValue(new Error("Erro ao iniciar pagamento"))

    const { result } = renderHook(() => useSelfCheckout())

    act(() => {
      result.current.actions.addProduct({ ean: "999", id: 1, name: "Produto", price: 19.9, quantity: 1 })
    })

    await act(async () => {
      await result.current.actions.handlePaymentConfirm("PIX")
    })

    expect(result.current.state.paymentError).toBe("Erro ao iniciar pagamento")
  })

  it("shows communication error when polling payment status fails", async () => {
    vi.useFakeTimers()
    vi.mocked(api.startPayment).mockResolvedValue(paymentProcessing)
    vi.mocked(api.fetchPaymentStatus).mockRejectedValue(new Error("Erro ao consultar pagamento"))

    const { result } = renderHook(() => useSelfCheckout())

    act(() => {
      result.current.actions.addProduct({ ean: "999", id: 1, name: "Produto", price: 19.9, quantity: 1 })
    })

    await act(async () => {
      const promise = result.current.actions.handlePaymentConfirm("PIX")
      await vi.runAllTimersAsync()
      await promise
    })

    expect(result.current.state.paymentError).toBe("Erro ao consultar pagamento")
  })
})
