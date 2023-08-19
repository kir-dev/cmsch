import { ProfileView } from '../../../util/views/profile.view'
import {
  Box,
  Button,
  Center,
  Divider,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  Text,
  useDisclosure
} from '@chakra-ui/react'
import QRCode from 'react-qr-code'
import { FaQrcode } from 'react-icons/fa'
import { Profile } from '../../../api/contexts/config/types'
import { WalletButton } from '../../../common-components/WalletButton'

export const ProfileQR = ({ profile, component }: { profile: ProfileView; component: Profile }) => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  return (
    <>
      <Divider my={10} borderWidth={2} w={'100%'} />
      <Center flexDirection="column">
        <Text fontSize="3xl" fontWeight={500}>
          {component.qrTitle}
        </Text>
        <Button mt={5} leftIcon={<FaQrcode />} onClick={onOpen}>
          QR kód felmutatása
        </Button>
        <Text fontSize="sm" color="brand.300" my={5}>
          vagy
        </Text>
        <WalletButton type="apple" name={profile.fullName} userId={profile.cmschId} />
        <WalletButton type="google" name={profile.fullName} userId={profile.cmschId} />
      </Center>

      <Modal isOpen={isOpen} onClose={onClose} isCentered>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{profile.fullName}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Center flexDirection="column">
              <Box w="fit-content" maxW="100%" p={2} mb={5} borderRadius={3} backgroundColor="white">
                <QRCode value={profile.cmschId} />
              </Box>
            </Center>
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  )
}
