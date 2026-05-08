"use client"

import WelcomeScreen from "@/components/welcome-screen"
import CpfScreen from "@/components/cpf-screen"
import ScanningScreen from "@/components/scanning-screen"
import PaymentScreen from "@/components/payment-screen"
import SuccessScreen from "@/components/success-screen"
import CancelConfirmation from "@/components/cancel-confirmation"
import AgeVerificationPopup from "@/components/age-verification-popup"
import AuthorizePricePopup from "@/components/authorize-price-popup"
import EmployeeCartPopup from "@/components/employee-cart-popup"
import PointsBlockedPopup from "@/components/points-blocked-popup"
import MyPointsPopup from "@/components/my-points-popup"
import CpfInputPopup from "@/components/cpf-input-popup"
import BarcodeInputPopup from "@/components/barcode-input-popup"
import { useSelfCheckout } from "@/hooks/use-self-checkout"
import type { Product } from "@/lib/types"

export default function Home() {
  const { actions, state } = useSelfCheckout()

  const handleCheckout = () => {
    if (state.cart.length === 0) {
      actions.setNotification("Adicione ao menos um item para continuar.")
      return
    }

    actions.setCurrentScreen("payment")
  }

  const handleMyPointsClick = () => {
    if (!state.hasCpf) {
      actions.setShowPointsBlockedPopup(true)
      return
    }

    actions.setShowMyPointsPopup(true)
  }

  const handleNewPurchase = () => {
    actions.setCompletedPurchase(null)
    actions.setCurrentScreen("welcome")
  }

  const handleOpenPriceAdjust = (product: Product) => {
    actions.setShowEmployeeCartPopup(false)
    actions.setSelectedProductForPriceAdjust(product)
  }

  return (
      <main className="w-screen h-screen flex items-center justify-center bg-background overflow-hidden">
        <div className="w-full h-full max-w-[1920px] max-h-[1080px] flex items-center justify-center p-6">
          {state.notification && (
              <div className="fixed top-4 left-1/2 z-50 -translate-x-1/2 rounded-md bg-black px-4 py-2 text-sm text-white shadow-lg">
                {state.notification}
              </div>
          )}
          {state.currentScreen === "welcome" && <WelcomeScreen onStartShopping={() => actions.setShowCpfPopup(true)} />}
          {state.currentScreen === "scanning" && (
              <ScanningScreen
                  cart={state.cart}
                  onUpdateQuantity={actions.updateProductQuantity}
                  onRemoveProduct={actions.removeProduct}
                  onAddProduct={actions.addProduct}
                  onCheckout={handleCheckout}
                  onCancel={() => actions.setShowCancelPopup(true)}
                  onAddBeerClick={() => actions.setShowAgeVerificationPopup(true)}
                  onMyPointsClick={handleMyPointsClick}
                  onBarcodeInputClick={() => actions.setShowBarcodeInputPopup(true)}
                  appliedCoupon={state.appliedCoupon}
                  pointsToEarn={state.pointsToEarn}
              />
          )}
          {state.currentScreen === "payment" && (
              <PaymentScreen
                  onConfirm={actions.handlePaymentConfirm}
                  onBack={() => actions.setCurrentScreen("scanning")}
                  cpf={state.cpf}
                  appliedCoupon={state.appliedCoupon}
              />
          )}
          {state.currentScreen === "success" && state.completedPurchase && (
              <SuccessScreen order={state.completedPurchase} onNewPurchase={handleNewPurchase} />
          )}
          {state.showCpfPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CpfScreen onSubmit={actions.handleCpfSubmit} onCancel={() => actions.setShowCpfPopup(false)} />
              </div>
          )}
          {state.showCancelPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CancelConfirmation onConfirm={actions.handleCancelConfirm} />
              </div>
          )}
          {state.showAgeVerificationPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <AgeVerificationPopup
                    onConfirm={actions.handleAgeVerificationConfirm}
                    onCancel={() => actions.setShowAgeVerificationPopup(false)}
                />
              </div>
          )}
          {state.showPointsBlockedPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <PointsBlockedPopup
                    onClose={() => actions.setShowPointsBlockedPopup(false)}
                    onInsertCpf={() => {
                      actions.setShowPointsBlockedPopup(false)
                      actions.setShowCpfInputPopup(true)
                    }}
                />
              </div>
          )}
          {state.showCpfInputPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CpfInputPopup onSubmit={actions.handleCpfInputSubmit} onCancel={() => actions.setShowCpfInputPopup(false)} />
              </div>
          )}
          {state.showMyPointsPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <MyPointsPopup
                    onClose={() => actions.setShowMyPointsPopup(false)}
                    pointsBalance={state.pointsBalance}
                    onApplyCoupon={actions.handleApplyCoupon}
                    appliedCoupon={state.appliedCoupon}
                    subtotal={state.subtotal}
                />
              </div>
          )}
          {state.showBarcodeInputPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <BarcodeInputPopup onSubmit={actions.handleBarcodeSubmit} onCancel={() => actions.setShowBarcodeInputPopup(false)} />
              </div>
          )}
          {state.showEmployeeCartPopup && state.employeeRegistration && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <EmployeeCartPopup
                    cart={state.cart}
                    employeeRegistration={state.employeeRegistration}
                    onSelectProduct={handleOpenPriceAdjust}
                    onClose={() => actions.setShowEmployeeCartPopup(false)}
                />
              </div>
          )}
          {state.selectedProductForPriceAdjust && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <AuthorizePricePopup
                    product={state.selectedProductForPriceAdjust}
                    employeeRegistration={state.employeeRegistration ?? ""}
                    onCancel={() => actions.setSelectedProductForPriceAdjust(null)}
                    onConfirm={actions.handleApplyPriceAdjust}
                />
              </div>
          )}
        </div>
      </main>
  );
}
