import { VotingFieldOption } from '../util/views/form.view'
import { Button, Collapse, Heading, HStack, Image, Radio, Stack, Text, VStack } from '@chakra-ui/react'

interface VotingFieldProps {
  onChange: (value?: string) => void
  value: string
  options: VotingFieldOption[]
  required: boolean
  disabled: boolean
}

export function VotingField({ options, onChange, value, required, disabled }: VotingFieldProps) {
  return (
    <VStack spacing={5} align="flex-start">
      <HStack justify="flex-end" w={'100%'}>
        <Collapse in={!required && !!value && !disabled}>
          <Button variant="outline" onClick={() => onChange('')}>
            Választásom törlése
          </Button>
        </Collapse>
      </HStack>
      {options
        .filter((item) => value === item.value || !disabled)
        .map((opt) => (
          <VotingFieldElement key={opt.value} onChange={() => onChange(opt.value)} selected={value === opt.value} option={opt} />
        ))}
    </VStack>
  )
}

interface VotingFieldElementProps {
  onChange: () => void
  selected: boolean
  option: VotingFieldOption
}

function VotingFieldElement({ onChange, selected, option }: VotingFieldElementProps) {
  return (
    <Stack
      alignItems="center"
      cursor="pointer"
      gap={5}
      onClick={onChange}
      borderRadius="md"
      borderColor={selected ? 'brand.200' : 'whiteAlpha.200'}
      borderWidth={1}
      position="relative"
      p={4}
      direction={['column', 'row']}
      w={'100%'}
    >
      {option.img && <Image borderRadius="md" width={40} objectFit="contain" src={option.img} alt={option.title} />}
      <VStack alignItems="flex-start">
        <Heading fontSize="3xl">{option.title}</Heading>
        <Text>{option.text}</Text>
      </VStack>
      <Radio position="absolute" top={5} right={5} size="lg" isChecked={selected} colorScheme="brand" />
    </Stack>
  )
}
