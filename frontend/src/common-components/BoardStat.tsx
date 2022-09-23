import { Stat, StatHelpText, StatLabel, StatNumber } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'

interface BoardStatProps {
  label: string
  value: string | number
  subValue?: string | number
  navigateTo?: string
}

export const BoardStat = ({ label, value, subValue, navigateTo }: BoardStatProps) => {
  const navigate = useNavigate()
  return (
    <Stat
      px={10}
      py={5}
      width="fit-content"
      borderRadius={10}
      borderColor="brand.500"
      borderWidth="1px"
      cursor={navigateTo ? 'pointer' : undefined}
      onClick={() => {
        if (navigateTo) navigate(navigateTo)
      }}
    >
      <StatLabel fontSize={15}>{label}</StatLabel>
      <StatNumber whiteSpace="nowrap" fontSize={30}>
        {value}
      </StatNumber>
      {subValue && <StatHelpText>{subValue}</StatHelpText>}
    </Stat>
  )
}
