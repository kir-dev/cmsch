import { Button, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay } from '@chakra-ui/react'
import GetSomeHelp from '../assets/stop-it-get-some-help-just-stop.gif'

type Props = {
  isOpen: boolean
  onClose: () => void
}

export const StopItModal = ({ isOpen, onClose }: Props) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Egy kicsit lassabban!</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <img alt="Stop spamming, get some help!" src={GetSomeHelp} />
        </ModalBody>

        <ModalFooter>
          <Button colorScheme="gray" mr={3} onClick={onClose}>
            Oké
          </Button>
          <Button colorScheme="brand" onClick={onClose}>
            Értettem
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}
