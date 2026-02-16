import { Button, ButtonGroup, Fade, Heading, Spinner } from '@chakra-ui/react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import { useNavigate } from 'react-router'

import { useEffect } from 'react'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useScanTokenMutation } from '../../api/hooks/token/useScanTokenMutation'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { QrReader } from '../../common-components/QrReader'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'
import { QRScanResultComponent } from './components/QRScanResultComponent'

const TokenScan = () => {
  const navigate = useNavigate()
  const { isLoggedIn } = useAuthContext()
  const spinnerColor = useBrandColor(500, 600)
  const brandColor = useBrandColor()
  const { mutate, isPending, isError, reset, data, isIdle } = useScanTokenMutation()

  const handleScan = (qrData: string | null) => {
    if (qrData) {
      mutate(qrData)
    }
  }

  useEffect(() => {
    if (!isLoggedIn) return
    const params = new URLSearchParams(location.search)
    if (params.has('token')) {
      mutate(location.href)
    }
  }, [isLoggedIn, mutate])

  return (
    <CmschPage loginRequired={true} title="QR beolvasás">
      <Heading mb={5}>QR beolvasás</Heading>
      {isPending && <Spinner color={spinnerColor} size="xl" thickness="0.3rem" />}
      {isIdle && <QrReader onScan={handleScan} />}

      {!isIdle && (
        <Fade in>
          <QRScanResultComponent response={data} isError={isError} />
        </Fade>
      )}

      <ButtonGroup mt={10}>
        <Button
          leftIcon={<FaArrowLeft />}
          onClick={() => {
            navigate(AbsolutePaths.TOKEN)
          }}
        >
          Vissza
        </Button>
        {!isIdle && (
          <Button colorScheme={brandColor} leftIcon={<FaQrcode />} onClick={reset}>
            Új QR scannelése
          </Button>
        )}
      </ButtonGroup>
    </CmschPage>
  )
}

export default TokenScan
