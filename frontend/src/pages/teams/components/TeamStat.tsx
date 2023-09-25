import { Box, CircularProgress, HStack, Stat, StatHelpText, StatLabel, StatNumber, StatProps } from '@chakra-ui/react'
import React from 'react'
import { Link } from 'react-router-dom'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { TeamStatView } from '../../../util/views/team.view'

interface TeamStatProps extends StatProps {
  stat: TeamStatView
}

export function TeamStat({ stat, ...props }: TeamStatProps) {
  const { name, value1, value2, navigate, percentage } = stat
  const background = useOpaqueBackground(1)
  const backgroundHover = useOpaqueBackground(2)
  const content = (
    <Stat borderRadius="lg" px={5} py={2} bg={background} _hover={{ bg: navigate ? backgroundHover : undefined }} {...props}>
      <HStack justify="space-between">
        <Box>
          <StatLabel>{name}</StatLabel>
          <StatNumber>{value1}</StatNumber>
          <StatHelpText>{value2}</StatHelpText>
        </Box>
        {typeof percentage !== 'undefined' && (
          <CircularProgress color="green.500" size={50} max={100} value={percentage}></CircularProgress>
        )}
      </HStack>
    </Stat>
  )

  if (navigate) return <Link to={navigate}>{content}</Link>
  return content
}
