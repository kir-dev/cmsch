import { Box, Flex, Select, Stack } from '@chakra-ui/react'
import { type Grammar, highlight, languages } from 'prismjs'
import 'prismjs/components/prism-c'
import 'prismjs/components/prism-cpp'
import 'prismjs/components/prism-csharp'
import 'prismjs/components/prism-java'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-kotlin'
import 'prismjs/components/prism-python'
import 'prismjs/components/prism-sql'
import 'prismjs/components/prism-typescript'
import 'prismjs/themes/prism-okaidia.css'
import { useState } from 'react'
import Editor from 'react-simple-code-editor'
import { CodeLanguage } from '../../../util/views/task.view'

interface CodeEditorProps {
  code: string
  setCode: React.Dispatch<React.SetStateAction<string>>
  readonly: boolean
}

const LANGUAGE_GRAMMAR_MAP = new Map<string, Grammar>([
  [CodeLanguage.C, languages.c],
  [CodeLanguage.CPP, languages.cpp],
  [CodeLanguage.CSHARP, languages.csharp],
  [CodeLanguage.JAVA, languages.java],
  [CodeLanguage.JAVASCRIPT, languages.javascript],
  [CodeLanguage.TYPESCRIPT, languages.typescript],
  [CodeLanguage.SQL, languages.sql],
  [CodeLanguage.KOTLIN, languages.kotlin],
  [CodeLanguage.PYTHON, languages.python]
])

const CodeEditor = ({ code, setCode, readonly }: CodeEditorProps) => {
  const [selectedLanguage, setSelectedLanguage] = useState<string>(CodeLanguage.C)
  return (
    <Stack mt={5}>
      <Flex justify="flex-end">
        <Select onChange={(e) => setSelectedLanguage(e.target.value)} w="10rem" value={selectedLanguage}>
          <option value={CodeLanguage.C}>C</option>
          <option value={CodeLanguage.CPP}>C++</option>
          <option value={CodeLanguage.CSHARP}>C#</option>
          <option value={CodeLanguage.JAVA}>JAVA</option>
          <option value={CodeLanguage.JAVASCRIPT}>JavaScript</option>
          <option value={CodeLanguage.TYPESCRIPT}>TypeScript</option>
          <option value={CodeLanguage.SQL}>SQL</option>
          <option value={CodeLanguage.KOTLIN}>Kotlin</option>
          <option value={CodeLanguage.PYTHON}>Python</option>
        </Select>
      </Flex>

      <Box my={5}>
        <Editor
          value={code}
          onValueChange={(code) => setCode(code)}
          highlight={(code) => highlight(code, LANGUAGE_GRAMMAR_MAP.get(selectedLanguage)!, selectedLanguage)}
          padding={10}
          readOnly={readonly}
          style={{
            fontFamily: '"Fira code", "Fira Mono", monospace',
            fontSize: 14,
            borderRadius: '0.375rem',
            backgroundColor: '#272822',
            color: 'white'
          }}
        />
      </Box>
    </Stack>
  )
}

export default CodeEditor
