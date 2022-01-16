import { CheckCircleIcon, InfoIcon, WarningIcon, WarningTwoIcon } from '@chakra-ui/icons'
import { Box, Center, Heading } from '@chakra-ui/react'
import React from 'react'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'

interface QrScanResultProps {
  response: ScanResponseDTO
}

export const QrScanResultComponent: React.FC<QrScanResultProps> = ({ response }: QrScanResultProps) => {
  const renderIcon = () => {
    switch (response.status) {
      case ScanStatus.SCANNED:
        return <CheckCircleIcon color="brand.500" boxSize="120px" />
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
        return 'Állomás lepecsételve'
      case ScanStatus.ALREADY_SCANNED:
        return 'Ezt a kódot már egyszer beolvastad'
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
          <Heading>{response.title}</Heading>
        </Center>
      )}
      <Center p="40px" mt="4">
        {renderIcon()}
      </Center>
      <Center>
        <Heading>{getInfoText()}</Heading>
      </Center>
    </Box>
  )
}
