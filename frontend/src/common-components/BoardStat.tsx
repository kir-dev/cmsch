import { Stat, StatHelpText, StatLabel, StatNumber, StatProps } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { useOpaqueBackground } from '../util/core-functions.util'

interface BoardStatProps extends StatProps {
  label: string
  value: string | number
  subValue?: string | number
  navigateTo?: string
}

export const BoardStat = ({ label, value, subValue, navigateTo, bg, ...props }: BoardStatProps) => {
  const background = useOpaqueBackground(1)
  const navigate = useNavigate()
  return (
    <Stat
      borderRadius="lg"
      px={5}
      py={2}
      bg={bg ?? background}
      cursor={navigateTo ? 'pointer' : undefined}
      onClick={() => {
        if (navigateTo) navigate(navigateTo)
      }}
      {...props}
    >
      <StatLabel>{label}</StatLabel>
      <StatNumber>{value}</StatNumber>
      {subValue && <StatHelpText>{subValue}</StatHelpText>}
    </Stat>
  )
}
