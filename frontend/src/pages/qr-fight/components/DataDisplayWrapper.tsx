import { Heading, HStack, Text, VStack } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'

import { QrArea } from '../../../util/views/qrFight.view'
import { AreaStatusBadge } from './AreaStatusBadge'
import { AreaDataDisplay } from './AreaDataDisplay'
import { AbsolutePaths } from '../../../util/paths'

interface DataDisplayWrapperProps {
  area: QrArea
}
export function DataDisplayWrapper({ area }: DataDisplayWrapperProps) {
  const navigate = useNavigate()
  return (
    <VStack
      p={5}
      mt={5}
      borderRadius={10}
      borderColor="brand.500"
      borderWidth="1px"
      cursor={area.unlocked ? 'pointer' : 'auto'}
      opacity={area.unlocked ? 1 : 0.2}
      _hover={{ backgroundColor: area.unlocked && 'brand.500' }}
      onClick={() => {
        if (area.unlocked) navigate(AbsolutePaths.QR_FIGHT + '/' + area.id)
      }}
    >
      <VStack align="flex-start" w="100%">
        <HStack>
          <Heading m={0} fontSize="lg">
            {area.level}. szint
          </Heading>
          <AreaStatusBadge area={area} />
        </HStack>
        {area.name && <Text>{area.name}</Text>}
      </VStack>
      <AreaDataDisplay area={area} />
    </VStack>
  )
}
