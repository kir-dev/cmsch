import * as React from 'react'
import { Box, Container, useColorModeValue } from '@chakra-ui/react'
import { BUGREPORT_URL, KIRDEV_URL } from '../../utils/configurations'

type FooterProps = {}

export const Footer: React.FC<FooterProps> = ({}) => (
  <Box borderTopWidth={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.700')}>
    <Container maxW="6xl" py={4}>
      {KIRDEV_URL} | {BUGREPORT_URL}
    </Container>
  </Box>
)
