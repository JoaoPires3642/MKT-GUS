"use client"

import { useEffect, useMemo, useState } from "react"
import {
  confirmPurchase,
  fetchPaymentStatus,
  fetchCustomerPoints,
  fetchProductByBarcode,
  mapBackendProduct,
  startPayment,
  verifyEmployeeRegistration,
} from "@/lib/api"
import type { ConfirmPurchaseResult, Coupon, PaymentMethod, PaymentStatus, PaymentTransactionResult, PriceOverride, Product } from "@/lib/types"

const POINTS_VALUE_PER_BLOCK = 5
const POINTS_PER_BLOCK = 10
const EMPLOYEE_BYPASS_CODE = "12345"

export function useSelfCheckout() {
  const [currentScreen, setCurrentScreen] = useState<"welcome" | "scanning" | "payment" | "success">("welcome")
  const [completedPurchase, setCompletedPurchase] = useState<ConfirmPurchaseResult | null>(null)
  const [showCpfPopup, setShowCpfPopup] = useState(false)
  const [showCancelPopup, setShowCancelPopup] = useState(false)
  const [showAgeVerificationPopup, setShowAgeVerificationPopup] = useState(false)
  const [showPointsBlockedPopup, setShowPointsBlockedPopup] = useState(false)
  const [showMyPointsPopup, setShowMyPointsPopup] = useState(false)
  const [showCpfInputPopup, setShowCpfInputPopup] = useState(false)
  const [showBarcodeInputPopup, setShowBarcodeInputPopup] = useState(false)
  const [showEmployeeCartPopup, setShowEmployeeCartPopup] = useState(false)
  const [employeeRegistration, setEmployeeRegistration] = useState<string | null>(null)
  const [employeeName, setEmployeeName] = useState<string | null>(null)
  const [selectedProductForPriceAdjust, setSelectedProductForPriceAdjust] = useState<Product | null>(null)
  const [cpf, setCpf] = useState("")
  const [cart, setCart] = useState<Product[]>([])
  const [hasCpf, setHasCpf] = useState(false)
  const [pointsBalance, setPointsBalance] = useState(1000)
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null)
  const [paymentTransaction, setPaymentTransaction] = useState<PaymentTransactionResult | null>(null)
  const [paymentStatus, setPaymentStatus] = useState<PaymentStatus | null>(null)
  const [paymentError, setPaymentError] = useState<string | null>(null)
  const [isProcessingPayment, setIsProcessingPayment] = useState(false)
  const [notification, setNotification] = useState<string | null>(null)
  const [pendingAdultProduct, setPendingAdultProduct] = useState<Product | null>(null)

  const subtotal = useMemo(() => cart.reduce((sum, item) => sum + item.price * item.quantity, 0), [cart])
  const pointsToEarn = useMemo(() => Math.floor(subtotal / POINTS_VALUE_PER_BLOCK) * POINTS_PER_BLOCK, [subtotal])

  const totalAmount = useMemo(() => {
    if (!appliedCoupon) {
      return subtotal
    }

    if (appliedCoupon.minPurchase && subtotal < appliedCoupon.minPurchase) {
      return subtotal
    }

    const discount =
      appliedCoupon.type === "percentage"
        ? Math.min(
            subtotal * (appliedCoupon.value / 100),
            appliedCoupon.maxDiscount ?? Number.POSITIVE_INFINITY,
          )
        : Math.min(appliedCoupon.value, subtotal)

    return subtotal - discount
  }, [appliedCoupon, subtotal])

  useEffect(() => {
    if (!notification) {
      return
    }

    const timer = setTimeout(() => setNotification(null), 3000)
    return () => clearTimeout(timer)
  }, [notification])

  const updateProductQuantity = (productId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      setCart((currentCart) => currentCart.filter((item) => item.id !== productId))
      return
    }

    setCart((currentCart) =>
      currentCart.map((item) => (item.id === productId ? { ...item, quantity: newQuantity } : item)),
    )
  }

  const addProduct = (product: Product) => {
    setCart((currentCart) => {
      const existingProduct = currentCart.find(
        (item) => item.ean === product.ean && !item.priceOverride && !product.priceOverride,
      )

      if (!existingProduct) {
        return [...currentCart, product]
      }

      return currentCart.map((item) =>
        item.id === existingProduct.id ? { ...item, quantity: item.quantity + 1 } : item,
      )
    })
  }

  const removeProduct = (productId: number) => {
    setCart((currentCart) => currentCart.filter((item) => item.id !== productId))
  }

  const handleCpfSubmit = async (value: string, points = 0) => {
    setCpf(value)
    setHasCpf(value.trim() !== "")
    setShowCpfPopup(false)
    setCurrentScreen("scanning")

    if (!value.trim()) {
      setPointsBalance(points)
      return
    }

    try {
      const data = await fetchCustomerPoints(value)
      setPointsBalance(typeof data === "number" ? data : data?.pontos ?? points)
    } catch {
      setNotification("Erro ao buscar pontos do cliente.")
    }
  }

  const handleCpfInputSubmit = (value: string) => {
    setCpf(value)
    setHasCpf(value.trim() !== "")
    setShowCpfInputPopup(false)
    setShowMyPointsPopup(true)
  }

  const handleCancelConfirm = (confirmed: boolean) => {
    setShowCancelPopup(false)
    if (!confirmed) {
      return
    }

    setCart([])
    setAppliedCoupon(null)
    setCompletedPurchase(null)
    setPaymentTransaction(null)
    setPaymentStatus(null)
    setPaymentError(null)
    setEmployeeRegistration(null)
    setEmployeeName(null)
    setCurrentScreen("welcome")
  }

  const handleAgeVerificationConfirm = async (employeeId: string, empName: string) => {
    setShowAgeVerificationPopup(false)
    setEmployeeRegistration(employeeId)
    setEmployeeName(empName)

    if (pendingAdultProduct) {
      addProduct(pendingAdultProduct)
      setPendingAdultProduct(null)
      return
    }

    try {
      const data = await fetchProductByBarcode("7896045503919")
      const product = mapBackendProduct(data, {
        image: "",
        isAdult: true,
        name: "Beck's Beer",
        price: 4.5,
        quantity: 1,
      })

      if (!product) {
        setNotification(data.message ?? "Produto não encontrado.")
        return
      }

      addProduct(product)
    } catch {
      setNotification("Erro ao adicionar cerveja. Tente novamente.")
    }
  }

  const handleAdultProductScanned = (product: Product) => {
    setPendingAdultProduct(product)
    setShowAgeVerificationPopup(true)
  }

  const handleAgeVerificationCancel = () => {
    setShowAgeVerificationPopup(false)
    setPendingAdultProduct(null)
  }

  const handleApplyCoupon = (coupon: Coupon) => {
    if (pointsBalance < coupon.pointsCost) {
      setNotification("Saldo de pontos insuficiente para aplicar este cupom.")
      return
    }

    if (coupon.minPurchase && subtotal < coupon.minPurchase) {
      setNotification(`O valor da compra (R$${subtotal.toFixed(2)}) é menor que o mínimo necessário (R$${coupon.minPurchase.toFixed(2)}).`)
      return
    }

    setAppliedCoupon(coupon)
  }

  const handleApplyPriceAdjust = (productId: number, newPrice: number, priceOverride: PriceOverride) => {
    setCart((currentCart) =>
      currentCart.map((item) =>
        item.id === productId
          ? { ...item, originalPrice: item.originalPrice ?? item.price, price: newPrice, priceOverride }
          : item,
      ),
    )
    setSelectedProductForPriceAdjust(null)
    setNotification(`Preco ajustado manualmente por ${employeeName ?? priceOverride.employeeRegistration}.`)
  }

  const handlePaymentConfirm = async (method: PaymentMethod) => {
    const pointsDeducted = appliedCoupon?.pointsCost ?? 0
    if (pointsDeducted > pointsBalance) {
      setNotification("Saldo de pontos insuficiente para aplicar este cupom.")
      return
    }

    try {
      setIsProcessingPayment(true)
      setPaymentError(null)

      const transaction = await startPayment({ amount: totalAmount, method })
      setPaymentTransaction(transaction)
      setPaymentStatus(transaction.status)

      const paidTransaction = await waitForConfirmedPayment(transaction.id)
      const result = await confirmPurchase({
        appliedCoupon,
        cart,
        cpf,
        hasCpf,
        paymentTransactionId: paidTransaction.id,
        ageVerifiedByRegistration: employeeRegistration ?? undefined,
      })

      if (typeof result.updatedPointsBalance === "number") {
        setPointsBalance(result.updatedPointsBalance)
      }

      setCompletedPurchase(result)
      setPaymentStatus(null)
      setCart([])
      setAppliedCoupon(null)
      setEmployeeRegistration(null)
      setEmployeeName(null)
      setCurrentScreen("success")
    } catch (error) {
      const message = error instanceof Error ? error.message : "Erro ao finalizar a compra. Tente novamente."
      setPaymentError(message)
      setNotification(message)
    } finally {
      setIsProcessingPayment(false)
    }
  }

  const waitForConfirmedPayment = async (paymentId: number) => {
    for (let attempt = 0; attempt < 10; attempt += 1) {
      const statusResult = await fetchPaymentStatus(paymentId)
      setPaymentTransaction(statusResult)
      setPaymentStatus(statusResult.status)

      if (statusResult.status === "PAID" || statusResult.status === "AUTHORIZED") {
        return statusResult
      }

      if (statusResult.status === "FAILED" || statusResult.status === "CANCELED" || statusResult.status === "EXPIRED") {
        throw new Error(statusResult.failureReason ?? "Pagamento nao foi aprovado.")
      }

      await new Promise((resolve) => setTimeout(resolve, 1000))
    }

    throw new Error("Pagamento ainda em processamento. Tente novamente em instantes.")
  }

  const handleBarcodeSubmit = async (barcode: string) => {
    setShowBarcodeInputPopup(false)
    const normalizedBarcode = barcode.trim().toUpperCase()

    try {
      if (cart.length > 0) {
        if (normalizedBarcode === EMPLOYEE_BYPASS_CODE) {
          setEmployeeRegistration(EMPLOYEE_BYPASS_CODE)
          setShowEmployeeCartPopup(true)
          setNotification("Bypass de funcionario ativado. Selecione um item para ajustar.")
          return
        }

        try {
          const employee = await verifyEmployeeRegistration(barcode)
          if (employee.valid) {
            setEmployeeRegistration(barcode)
            setEmployeeName(employee.name ?? "")
            setShowEmployeeCartPopup(true)
            setNotification(`Funcionario ${employee.name ?? barcode} validado. Selecione um item para ajustar.`)
            return
          }
        } catch {
          // se nao for matricula valida, segue para a busca do produto
        }
      }

      const data = await fetchProductByBarcode(barcode)
      const product = mapBackendProduct(data)
      if (!product) {
        setNotification(data.message ?? `Produto não encontrado para o código: ${barcode}`)
        return
      }

      if (product.isAdult) {
        setPendingAdultProduct(product)
        setShowAgeVerificationPopup(true)
        return
      }

      addProduct(product)
    } catch {
      setNotification(`Erro ao buscar produto para o código: ${barcode}`)
    }
  }

  return {
    actions: {
      addProduct,
      handleAdultProductScanned,
      handleAgeVerificationCancel,
      handleAgeVerificationConfirm,
      handleApplyCoupon,
      handleApplyPriceAdjust,
      handleBarcodeSubmit,
      handleCancelConfirm,
      handleCpfInputSubmit,
      handleCpfSubmit,
      handlePaymentConfirm,
      removeProduct,
      setCompletedPurchase,
      setCurrentScreen,
      setSelectedProductForPriceAdjust,
      setShowAgeVerificationPopup,
      setShowBarcodeInputPopup,
      setShowCancelPopup,
      setShowCpfInputPopup,
      setShowCpfPopup,
      setShowEmployeeCartPopup,
      setShowMyPointsPopup,
      setNotification,
      setShowPointsBlockedPopup,
      updateProductQuantity,
    },
    state: {
      appliedCoupon,
      cart,
      completedPurchase,
      cpf,
      currentScreen,
      employeeName,
      employeeRegistration,
      hasCpf,
      notification,
      isProcessingPayment,
      pointsBalance,
      pointsToEarn,
      paymentError,
      paymentStatus,
      paymentTransaction,
      selectedProductForPriceAdjust,
      showAgeVerificationPopup,
      showBarcodeInputPopup,
      showCancelPopup,
      showCpfInputPopup,
      showCpfPopup,
      showEmployeeCartPopup,
      showMyPointsPopup,
      showPointsBlockedPopup,
      subtotal,
      totalAmount,
    },
  }
}
