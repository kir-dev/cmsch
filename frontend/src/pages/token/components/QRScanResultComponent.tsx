import { CheckCircleIcon, InfoIcon, WarningIcon, WarningTwoIcon } from '@chakra-ui/icons'
import { Box, Center, Heading } from '@chakra-ui/react'
import { ScanResponseView, ScanStatus } from '../../../util/views/token.view'

interface QrScanResultProps {
  response: ScanResponseView
}

export const QRScanResultComponent = ({ response }: QrScanResultProps) => {
  const renderIcon = () => {
    switch (response.status) {
      case ScanStatus.SCANNED:
        return <CheckCircleIcon color="green.500" boxSize="120px" />
      case ScanStatus.ALREADY_SCANNED:
        return <InfoIcon color="orange.500" boxSize="120px" />
      case ScanStatus.WRONG:
        return <WarningTwoIcon color="red.500" boxSize="120px" />
      default:
        return <WarningIcon color="red.600" boxSize="120px" />
    }
  }

  const getInfoText = () => {
    switch (response.status) {
      case ScanStatus.SCANNED:
        return 'QR kód feljegyezve'
      case ScanStatus.ALREADY_SCANNED:
        return 'Ezt a kódot már egyszer beolvasta'
      case ScanStatus.WRONG:
        return 'Ez a QR-kód nem jó a pontgyűjtéshez'
      default:
        return 'Hibás státusz kód'
    }
  }

  return (
    <Box>
      {response.title && (
        <Center>
          <Heading size="md">{response.title}</Heading>
        </Center>
      )}
      <Center p="40px" mt="4">
        {renderIcon()}
      </Center>
      <Center>
        <Heading mb={5} size="md">
          {getInfoText()}
        </Heading>
      </Center>
    </Box>
  )
}
