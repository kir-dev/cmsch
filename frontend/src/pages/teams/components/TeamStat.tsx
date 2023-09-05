import { Box, CircularProgress, HStack, Stat, StatHelpText, StatLabel, StatNumber, StatProps } from '@chakra-ui/react'
import React from 'react'
import { IconType } from 'react-icons/lib'
import { useOpaqueBackground } from '../../../util/core-functions.util'

interface TeamStatProps extends StatProps {
  label: string
  value: number
  maxValue?: number
  helpText: string
  icon?: IconType
}

export function TeamStat({ label, value, maxValue, helpText, bg, icon, ...props }: TeamStatProps) {
  const background = useOpaqueBackground(1)
  return (
    <Stat borderRadius="lg" px={5} py={2} bg={bg ?? background} {...props}>
      <HStack justify="space-between">
        <Box>
          <StatLabel>{label}</StatLabel>
          {!maxValue && <StatNumber>{value}</StatNumber>}
          {maxValue && <StatNumber>{`${value} / ${maxValue}`}</StatNumber>}
          <StatHelpText>{helpText}</StatHelpText>
        </Box>
        {maxValue && <CircularProgress color="green.500" size={50} max={maxValue} value={value}></CircularProgress>}
        {icon && icon({ size: 40 })}
      </HStack>
    </Stat>
  )
}
