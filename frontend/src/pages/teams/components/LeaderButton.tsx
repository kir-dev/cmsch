import { Button, Tooltip } from '@chakra-ui/react'
import { FaChessKing } from 'react-icons/fa'

interface LeaderButtonProps {
  onPromoteLeadership: () => void
}

export function LeaderButton({ onPromoteLeadership }: LeaderButtonProps) {
  return (
    <Tooltip label="Csapatkapitánnyá tevés">
      <Button colorScheme="blue" variant="outline" onClick={onPromoteLeadership}>
        <FaChessKing />
      </Button>
    </Tooltip>
  )
}
