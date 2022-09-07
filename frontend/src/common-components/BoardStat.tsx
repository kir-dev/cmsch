import { Stat, StatLabel, StatNumber } from '@chakra-ui/react'

interface BoardStatProps {
  label: string
  value: string | number
}

export const BoardStat = ({ label, value }: BoardStatProps) => {
  return (
    <Stat px={10} py={5} borderRadius={10} borderColor="brand.500" borderWidth="1px">
      <StatLabel fontSize={15}>{label}</StatLabel>
      <StatNumber fontSize={30}>{value}</StatNumber>
    </Stat>
  )
}
