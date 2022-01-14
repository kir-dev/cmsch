import * as React from 'react'
import { Box } from '@chakra-ui/react'
import { BUGREPORT_URL, KIRDEV_URL } from '../../utils/configurations'

type FooterProps = {}

export const Footer: React.FC<FooterProps> = ({}) => (
  <Box>
    {KIRDEV_URL} | {BUGREPORT_URL}
  </Box>
)
