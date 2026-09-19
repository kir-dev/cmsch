import { Alert, AlertDescription } from '@/components/ui/alert'
import { l } from '@/util/language'
import { BrowserCodeReader, BrowserQRCodeReader, type IScannerControls } from '@zxing/browser'
import { AlertCircle } from 'lucide-react'
import { useEffect, useMemo, useRef, useState } from 'react'

interface QrReaderProps {
  onScan: (data: string) => void
}

const selectCamera = async (): Promise<string | undefined> => {
  let cameras = await BrowserCodeReader.listVideoInputDevices()
  cameras = cameras.filter((camera) => !camera.label.toLowerCase().includes('virtual'))
  return cameras.at(-1)?.deviceId
}

export function QrReader({ onScan }: QrReaderProps) {
  const codeReader = useMemo(() => new BrowserQRCodeReader(), [])
  const [error, setError] = useState<string | null>(null)
  const videoElement = useRef<HTMLVideoElement>(null)
  const onScanRef = useRef(onScan)
  const decodeRef = useRef(Promise.resolve())

  useEffect(() => {
    onScanRef.current = onScan
  }, [onScan])

  useEffect(() => {
    let controls: IScannerControls | null
    let cancelled = false

    async function decodeContinuously() {
      try {
        await decodeRef.current
        if (!videoElement.current) return
        if (cancelled) return
        const selectedDeviceId = await selectCamera()
        controls = await codeReader.decodeFromVideoDevice(selectedDeviceId, videoElement.current, (result, _err, controls) => {
          if (result) {
            controls.stop()
            onScanRef.current(result.getText())
          }
        })
      } catch {
        if (!cancelled) setError(l('qr-reader-camera-error'))
      }
    }

    decodeRef.current = decodeContinuously()

    return () => {
      decodeRef.current
        .then(() => {
          cancelled = true
          controls?.stop()
        })
        .catch(() => {})
    }
  }, [codeReader])

  if (error) {
    return (
      <Alert variant="destructive" className="mx-auto w-fit">
        <AlertCircle className="h-5 w-5" />
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    )
  }

  return (
    <div className="w-fit h-fit rounded-lg overflow-hidden mx-auto">
      <video height="100%" width="100%" className="object-cover" ref={videoElement} />
    </div>
  )
}
