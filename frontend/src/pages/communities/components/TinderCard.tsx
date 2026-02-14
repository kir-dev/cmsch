import { Box, Heading, HStack, IconButton, Image, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { FaHeart, FaTimes } from 'react-icons/fa'

import type { TinderCommunity } from '../../../util/views/tinder'

type Props = {
  data: TinderCommunity
  depth?: number
  onLike?: (c: TinderCommunity) => void
  onDislike?: (c: TinderCommunity) => void
  className?: string
}

export const TinderCard = ({ data, depth = 0, onLike, onDislike, className }: Props) => {
  // Prefer the typed `TinderCommunity` fields and use optional chaining/fallbacks.
  const title = data?.name || 'Unknown community'
  const description = data?.shortDescription || ''
  const image = data?.logo || ''

  const bg = useColorModeValue('white', 'gray.800')
  const infoColor = useColorModeValue('gray.600', 'gray.300')
  const placeholderBg = useColorModeValue('#efefef', '#2A2A2A')

  return (
    <Box
      className={className}
      role="article"
      aria-label={title}
      data-depth={depth}
      bg={bg}
      w="416px" /* 320 * 1.3 */
      h="598px" /* 460 * 1.3 */
      borderRadius="16px" /* ~12 * 1.3 */
      boxShadow="lg"
      overflowY="auto"
      sx={{ WebkitOverflowScrolling: 'touch' }}
      userSelect="none"
      transition="transform 160ms ease"
      display="flex"
      flexDirection="column"
    >
      {image ? (
        <Image src={image} alt={title} objectFit="cover" h="416px" w="100%" borderTopRadius="16px" />
      ) : (
        <Box h="416px" bg={placeholderBg} borderTopRadius="16px" />
      )}

      <VStack align="stretch" p={3} flex="1">
        <Heading as="h3" size="md">
          {title}
        </Heading>

        <Text fontSize="sm" color={infoColor}>
          Alapítva: {data?.established ?? '—'}
        </Text>
        <Text fontSize="sm" color={infoColor}>
          Reszort: {data?.resortName ?? '—'}
        </Text>

        <HStack justify="space-around" mt={2}>
          <IconButton
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Dislike"
            title="Dislike"
            icon={<FaTimes />}
            onClick={() => onDislike && onDislike(data)}
            variant="outline"
            colorScheme="red"
            borderRadius="full"
            w="56px" /* 44 * 1.3 ~= 57.2 -> 56 */
            h="56px"
            minW="56px"
            minH="56px"
            p={0}
          />

          <IconButton
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Like"
            title="Like"
            icon={<FaHeart />}
            onClick={() => onLike && onLike(data)}
            bg="cyan.500"
            color="white"
            _hover={{ bg: 'cyan.600' }}
            borderRadius="full"
            w="56px"
            h="56px"
            minW="56px"
            minH="56px"
            p={0}
          />
        </HStack>

        <Box
          mt={2}
          h="182px"
          overflowY="auto"
          color={useColorModeValue('#555', '#ccc')}
          fontSize="sm"
          lineHeight="1.4"
          aria-hidden={description.length === 0}
        >
          {description ? <Text whiteSpace="normal">{description}</Text> : <Text color="gray.400">No description</Text>}
        </Box>
      </VStack>
    </Box>
  )
}
