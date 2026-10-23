import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { type PropsWithChildren, useEffect, useMemo, useState } from 'react'

export const Popup = ({ children }: PropsWithChildren) => {
  const component = useConfigContext()?.components?.popup
  const popupUrls = useMemo(
    () =>
      component?.popupUrls
        .split(',')
        .map((url) => url.trim())
        .filter((url) => url.length > 0) ?? [],
    [component?.popupUrls]
  )
  const popupConfigKey = [
    component?.popupEnabled,
    component?.intervalLowBound,
    component?.intervalHighBound,
    component?.popupBackground,
    component?.popupOpacity,
    popupUrls.join(',')
  ].join('|')
  const [popup, setPopup] = useState<{ url: string; configKey: string }>()
  //const [timerKey, setTimerKey] = useState(0)

  useEffect(() => {
    if (!component?.popupEnabled || popupUrls.length === 0) return

    const lowBound = Math.max(0, component.intervalLowBound)
    const highBound = Math.max(lowBound, component.intervalHighBound)
    const intervalSeconds = Math.random() * (highBound - lowBound) + lowBound
    const timer = window.setTimeout(() => {
      const randomUrl = popupUrls[Math.floor(Math.random() * popupUrls.length)]
      if (randomUrl) setPopup({ url: randomUrl, configKey: popupConfigKey })
    }, intervalSeconds * 1000)

    return () => window.clearTimeout(timer)
  }, [component?.intervalHighBound, component?.intervalLowBound, component?.popupEnabled, popupConfigKey, popupUrls /*, timerKey*/])

  const dismissPopup = () => {
    setPopup(undefined)
    //setTimerKey((key) => key + 1)
  }

  return (
    <>
      {children}
      {popup?.configKey === popupConfigKey && component?.popupEnabled && (
        <div className="fixed inset-0 z-50 flex h-screen w-screen items-center justify-center" onClick={dismissPopup} role="presentation">
          <div
            className="absolute inset-0"
            style={{
              backgroundColor: component?.popupBackground,
              opacity: Math.min(100, Math.max(0, component?.popupOpacity ?? 100)) / 100
            }}
          />
          <img alt="" className="relative max-h-full max-w-full object-contain" src={popup.url} />
        </div>
      )}
    </>
  )
}
