import * as api from "@/lib/api"

describe("api payment helpers", () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it("sends paymentTransactionId when confirming purchase", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ id: 1 }),
    })
    vi.stubGlobal("fetch", fetchMock)

    await api.confirmPurchase({
      appliedCoupon: null,
      cart: [{ ean: "999", id: 1, name: "Produto", price: 19.9, quantity: 1 }],
      cpf: "529.982.247-25",
      hasCpf: true,
      paymentTransactionId: 7,
    })

    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/pedidos/confirmar-compra",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({
          clienteCpf: "52998224725",
          paymentTransactionId: 7,
          itens: [
            {
              ajustePreco: null,
              ean: "999",
              quantidade: 1,
              valorUnitario: 19.9,
            },
          ],
          cupom: null,
        }),
      }),
    )
  })

  it("throws backend message when starting payment fails", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: false,
      json: async () => ({ message: "Gateway indisponivel" }),
    })
    vi.stubGlobal("fetch", fetchMock)

    await expect(api.startPayment({ amount: 19.9, method: "PIX" })).rejects.toThrow("Gateway indisponivel")
  })

  it("throws backend message when fetching payment status fails", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: false,
      json: async () => ({ message: "Transacao nao encontrada" }),
    })
    vi.stubGlobal("fetch", fetchMock)

    await expect(api.fetchPaymentStatus(99)).rejects.toThrow("Transacao nao encontrada")
  })

  it("maps backend product with fallback data", () => {
    const product = api.mapBackendProduct(
      { ean: "999", nome: "Produto", valor: 10, produtoMaiorDeIdade: true },
      { image: "/img.png", quantity: 2 },
    )

    expect(product).toEqual(
      expect.objectContaining({
        ean: "999",
        name: "Produto",
        price: 10,
        isAdult: true,
        image: "/img.png",
        quantity: 2,
      }),
    )
  })

  it("returns null when backend product has no ean", () => {
    expect(api.mapBackendProduct({ name: "Sem EAN" })).toBeNull()
  })
})
