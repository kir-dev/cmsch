import { BrowserCodeReader, BrowserQRCodeReader, IScannerControls } from '@zxing/browser'
import { useEffect, useRef } from 'react'

interface QrReaderProps {
  onScan: (data: string) => void
}

const selectCamera = async (): Promise<string> => {
  let cameras = await BrowserCodeReader.listVideoInputDevices()
  cameras = cameras.filter((camera) => !camera.label.toLowerCase().includes('virtual'))
  return cameras[cameras.length - 1].deviceId
}

export function QrReader({ onScan }: QrReaderProps) {
  const codeReader = useRef(new BrowserQRCodeReader())
  const videoElement = useRef<HTMLVideoElement>(null)

  useEffect(() => {
    let controls: IScannerControls | null
    let cancelled = false

    async function decodeContinuously() {
      if (!videoElement.current) return
      if (cancelled) return
      const selectedDeviceId = await selectCamera()
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
