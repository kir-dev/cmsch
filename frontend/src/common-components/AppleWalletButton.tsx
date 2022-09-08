import AppleWalletImage from '../assets/AppleWalletHu.svg'
import { PASS_SERVER_URL } from '../util/configs/environment.config'
import { useMemo } from 'react'
import { Center, Image, Text } from '@chakra-ui/react'

interface AppleWalletButtonProps {
  queryDto: Record<string, string>
}

export function AppleWalletButton({ queryDto }: AppleWalletButtonProps) {
  const url = useMemo(() => {
    if (!PASS_SERVER_URL) return
    const urlFactory = new URL(PASS_SERVER_URL)
    Object.keys(queryDto).forEach((key) => {
      urlFactory.searchParams.append(key, queryDto[key])
    })
    return urlFactory.toString()
  }, [PASS_SERVER_URL, queryDto])

  if (!url) return null

  return (
    <Center maxW={80} flexDirection="column" mt={5}>
      <Text fontSize="sm" color="brand.300">
        vagy
      </Text>
      <Image
        cursor="pointer"
        maxW="100%"
        w={48}
        m={5}
        src={AppleWalletImage}
        onClick={() => {
          if (!url) return
          window.open(url)
        }}
      />
      <Text fontSize="sm" color="brand.300">
        Apple felhasználók részére (Beta)
      </Text>
    </Center>
  )
}
