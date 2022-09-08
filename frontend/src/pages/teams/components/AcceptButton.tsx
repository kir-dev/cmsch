import { Button } from '@chakra-ui/react'
import { CheckIcon } from '@chakra-ui/icons'

interface AcceptButtonProps {
  onAccept: () => void
}

export function AcceptButton({ onAccept }: AcceptButtonProps) {
  return (
    <Button colorScheme="green" variant="outline" onClick={onAccept}>
      <CheckIcon />
    </Button>
  )
}
