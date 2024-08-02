import { BrowserCodeReader, BrowserQRCodeReader, IScannerControls } from '@zxing/browser'
import { useEffect, useRef } from 'react'

interface QrReaderProps {
  onScan: (data: string) => void
}

export function QrReader({ onScan }: QrReaderProps) {
  const codeReader = useRef(new BrowserQRCodeReader())
  const videoElement = useRef<HTMLVideoElement>(null)

  useEffect(() => {
    let controls: IScannerControls | null
    let cancelled = false

    async function decodeContinuously() {
      if (!videoElement.current) return
      const videoInputDevices = await BrowserCodeReader.listVideoInputDevices()
      const selectedDeviceId = videoInputDevices[0].deviceId
      if (cancelled) return
      controls = await codeReader.current.decodeFromVideoDevice(selectedDeviceId, videoElement.current, (result, _err, controls) => {
        if (result) {
          controls.stop()
          onScan(result.getText())
        }
      })

      if (cancelled) controls.stop()
    }

    decodeContinuously()

    return () => {
      cancelled = true
      controls?.stop()
    }
  }, [onScan, videoElement])
  return (
    <div className=" w-fit h-fit rounded-lg overflow-hidden mx-auto">
      <video height="100%" width="100%" className="object-cover" ref={videoElement} />
    </div>
  )
}
