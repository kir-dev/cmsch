import { useNavigate } from 'react-router-dom'
import { useOpaqueBackground } from '../util/core-functions.util'
import { StatHelpText, StatLabel, StatRoot, StatValueText } from '../components/ui/stat.tsx'
import { StatRootProps } from '@chakra-ui/react'

interface BoardStatProps extends StatRootProps {
  label: string
  value: string | number
  subValue?: string | number
  navigateTo?: string
}

export const BoardStat = ({ label, value, subValue, navigateTo, bg, ...props }: BoardStatProps) => {
  const background = useOpaqueBackground(1)
  const navigate = useNavigate()
  return (
    <StatRoot
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
      <StatValueText>{value}</StatValueText>
      {subValue && <StatHelpText>{subValue}</StatHelpText>}
    </StatRoot>
  )
}
