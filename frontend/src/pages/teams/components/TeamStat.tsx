import { Box, CircularProgress, HStack, Stat, StatHelpText, StatLabel, StatNumber, StatProps } from '@chakra-ui/react'
import React from 'react'
import { IconType } from 'react-icons/lib'
import { Link } from 'react-router-dom'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'

interface TeamStatProps extends StatProps {
  label: string
  value: number | string
  percentage?: number
  helpText?: number | string
  icon?: IconType
  categoryId?: string
}

export function TeamStat({ label, value, percentage, helpText, bg, icon, categoryId, ...props }: TeamStatProps) {
  const background = useOpaqueBackground(1)
  const backgroundHover = useOpaqueBackground(2)
  const content = (
    <Stat borderRadius="lg" px={5} py={2} bg={bg ?? background} _hover={{ bg: url ? backgroundHover : undefined }} {...props}>
      <HStack justify="space-between">
        <Box>
          <StatLabel>{label}</StatLabel>
          <StatNumber>{value}</StatNumber>
          <StatHelpText>{helpText}</StatHelpText>
        </Box>
        {typeof percentage !== 'undefined' && (
          <CircularProgress color="green.500" size={50} max={100} value={percentage}></CircularProgress>
        )}
        {icon && icon({ size: 40 })}
      </HStack>
    </Stat>
  )

  if (categoryId) return <Link to={`${AbsolutePaths.TASKS}/category/${categoryId}`}>{content}</Link>
  return content
}
