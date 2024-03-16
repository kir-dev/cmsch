import { CheckCircleIcon, CloseIcon, InfoIcon, WarningIcon, WarningTwoIcon } from '@chakra-ui/icons'
import { Box, Center, Heading, Image, Text } from '@chakra-ui/react'
import { ScanMessages, ScanResponseView, ScanStatus } from '../../../util/views/token.view'
import Markdown from '../../../common-components/Markdown'

interface QrScanResultProps {
  isError?: boolean
  response?: ScanResponseView
}

export const QRScanResultComponent = ({ response, isError }: QrScanResultProps) => {
  const renderIcon = () => {
    if (!response?.status || isError) {
      return <CloseIcon color="red.500" boxSize="120px" />
    } else
      switch (response.status) {
        case ScanStatus.SCANNED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.ALREADY_SCANNED:
          return <InfoIcon color="blue.500" boxSize="120px" />
        case ScanStatus.WRONG:
          return <WarningTwoIcon color="yellow.500" boxSize="120px" />
        case ScanStatus.CANNOT_COLLECT:
          return <WarningTwoIcon color="yellow.500" boxSize="120px" />
        case ScanStatus.INACTIVE:
          return <WarningTwoIcon color="yellow.500" boxSize="120px" />
        case ScanStatus.QR_FIGHT_LEVEL_LOCKED:
          return <InfoIcon color="orange.500" boxSize="120px" />
        case ScanStatus.QR_FIGHT_LEVEL_NOT_OPEN:
          return <InfoIcon color="orange.500" boxSize="120px" />
        case ScanStatus.QR_FIGHT_TOWER_LOCKED:
          return <InfoIcon color="orange.500" boxSize="120px" />
        case ScanStatus.QR_TOWER_CAPTURED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.QR_TOWER_LOGGED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.QR_TOWER_ENSLAVED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.QR_TOWER_ALREADY_ENSLAVED:
          return <InfoIcon color="orange.500" boxSize="120px" />
        case ScanStatus.QR_TOTEM_LOGGED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.QR_TOTEM_ENSLAVED:
          return <CheckCircleIcon color="green.500" boxSize="120px" />
        case ScanStatus.QR_TOTEM_ALREADY_ENSLAVED:
          return <InfoIcon color="orange.500" boxSize="120px" />
        case ScanStatus.QR_FIGHT_TOTEM_LOCKED:
          return <InfoIcon color="orange.500" boxSize="120px" />
        default:
          return <WarningIcon color="red.500" boxSize="120px" />
      }
  }

  return (
    <Box textAlign="center" maxW="sm" mx="auto">
      <Center flexDirection="column" p="40px" mt="4">
        {response?.iconUrl ? <Image src={response.iconUrl} /> : renderIcon()}
        {response?.title && <Heading size="lg">{response.title}</Heading>}
        {!response?.title && isError && <Heading>Hiba</Heading>}
        {response && <Text fontSize="lg">{ScanMessages[response.status] || 'Ismeretlen eredmény'}</Text>}
        {!response && isError && <Text fontSize="lg">Hiba a validálás során!</Text>}
        {response?.description && (
          <Box py="4">
            <Markdown text={response.description} />
          </Box>
        )}
      </Center>
    </Box>
  )
}
