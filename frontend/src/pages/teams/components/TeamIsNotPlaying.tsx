import { Box, Flex, Heading, Text } from '@chakra-ui/react'
import { CmschPage } from '../../../common-components/layout/CmschPage'

export function TeamIsNotPlaying() {
  return (
    <CmschPage>
      <Flex align="center" textAlign="center" direction="column">
        <Box>
          <Heading mb={2}>A csapat nem látható</Heading>
          <Text>Ez a csapat nincs játékban, vagy el van rejtve.</Text>
        </Box>
      </Flex>
    </CmschPage>
  )
}
