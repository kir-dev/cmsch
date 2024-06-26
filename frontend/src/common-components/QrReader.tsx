import { BrowserCodeReader, BrowserQRCodeReader, IScannerControls } from '@zxing/browser'
import { useEffect, useRef } from 'react'

interface QrReaderProps {
  onScan: (data: string) => void
}

export function QrReader({ onScan }: QrReaderProps) {
  const codeReader = useRef(new BrowserQRCodeReader())
  const videoElement = useRef<HTMLVideoElement>(null)
  const controls = useRef<IScannerControls>()

  async function decodeContinuously() {
    if (!videoElement.current) return
    const videoInputDevices = await BrowserCodeReader.listVideoInputDevices()
    const selectedDeviceId = videoInputDevices[0].deviceId
    controls.current = await codeReader.current.decodeFromVideoDevice(selectedDeviceId, videoElement.current, (result, err, controls) => {
      console.debug(result, err)
      if (result) {
        controls.stop()
        onScan(result.getText())
      }
    })
  }

  useEffect(() => {
    decodeContinuously()
    return () => {
      controls.current?.stop()
    }
  }, [])
  return (
    <div className=" w-fit h-fit rounded-lg overflow-hidden mx-auto">
      <video height="100%" width="100%" className="object-cover" ref={videoElement} />
    </div>
  )
}
