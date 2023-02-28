import { Button, ButtonGroup, Fade, Heading, Spinner, useColorModeValue } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import QRreader from 'react-qr-reader'
import { useNavigate } from 'react-router-dom'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { QRScanResultComponent } from './components/QRScanResultComponent'
import { useScanTokenMutation } from '../../api/hooks/token/useScanTokenMutation'
import { useEffect } from 'react'
import { AbsolutePaths } from '../../util/paths'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'

const TokenScan = () => {
  const navigate = useNavigate()
  const { isLoggedIn } = useAuthContext()
  const spinnerColor = useColorModeValue('brand.500', 'brand.600')
  const { mutate, isLoading, isError, reset, data, isIdle } = useScanTokenMutation()

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
  }, [])

  const handleError = () => {}

  return (
    <CmschPage loginRequired>
      <Helmet title="QR beolvasás" />
      <Heading mb={5}>QR beolvasás</Heading>
      {isLoading && <Spinner color={spinnerColor} size="xl" thickness="0.3rem" />}
      {isIdle && <QRreader delay={300} onError={handleError} onScan={handleScan} />}

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
          <Button colorScheme="brand" leftIcon={<FaQrcode />} onClick={reset}>
            Új QR scannelése
          </Button>
        )}
      </ButtonGroup>
    </CmschPage>
  )
}

export default TokenScan
