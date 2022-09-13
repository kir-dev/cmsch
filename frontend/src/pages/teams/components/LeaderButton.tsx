import { Button } from '@chakra-ui/react'
import { FaChessKing } from 'react-icons/fa'

interface LeaderButtonProps {
  onPromoteLeadership: () => void
}

export function LeaderButton({ onPromoteLeadership }: LeaderButtonProps) {
  return (
    <Button colorScheme="blue" variant="outline" onClick={onPromoteLeadership}>
      <FaChessKing />
    </Button>
  )
}
