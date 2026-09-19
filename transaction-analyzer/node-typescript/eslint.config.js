const tsParser = require('@typescript-eslint/parser');
const tsPlugin = require('@typescript-eslint/eslint-plugin');
const jsdoc = require('eslint-plugin-jsdoc');
const preferArrow = require('eslint-plugin-prefer-arrow');
const prettierRecommended = require('eslint-plugin-prettier/recommended');

module.exports = [
  {
    ignores: ['dist/**', 'coverage/**', 'node_modules/**']
  },

  // Replaces the old "plugin:@typescript-eslint/recommended" and
  // "plugin:@typescript-eslint/recommended-requiring-type-checking" extends.
  // The type-checked preset already includes the non-type-checked rules.
  ...tsPlugin.configs['flat/recommended-type-checked'],

  {
    files: ['**/*.ts'],
    languageOptions: {
      parser: tsParser,
      ecmaVersion: 2020,
      sourceType: 'module',
      parserOptions: {
        projectService: true,
        tsconfigRootDir: __dirname
      }
    },
    plugins: {
      jsdoc,
      'prefer-arrow': preferArrow
    },
    rules: {
      '@typescript-eslint/adjacent-overload-signatures': 'error',
      '@typescript-eslint/array-type': ['error', { default: 'array' }],
      '@typescript-eslint/await-thenable': 'error',
      '@typescript-eslint/ban-ts-comment': 'error',
      '@typescript-eslint/consistent-type-assertions': 'error',
      '@typescript-eslint/dot-notation': 'error',
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/naming-convention': [
        'error',
        {
          selector: 'variable',
          format: ['camelCase', 'UPPER_CASE', 'PascalCase'],
          leadingUnderscore: 'allow',
          trailingUnderscore: 'forbid'
        }
      ],
      '@typescript-eslint/no-array-constructor': 'error',
      '@typescript-eslint/no-empty-function': 'error',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-extra-non-null-assertion': 'error',
      '@typescript-eslint/no-floating-promises': 'error',
      '@typescript-eslint/no-for-in-array': 'error',
      '@typescript-eslint/no-implied-eval': 'error',
      '@typescript-eslint/no-inferrable-types': 'error',
      '@typescript-eslint/no-misused-new': 'error',
      '@typescript-eslint/no-misused-promises': 'error',
      '@typescript-eslint/no-namespace': 'error',
      '@typescript-eslint/no-non-null-asserted-optional-chain': 'error',
      '@typescript-eslint/no-non-null-assertion': 'warn',
      '@typescript-eslint/no-require-imports': 'error',
      '@typescript-eslint/no-shadow': ['error', { hoist: 'all' }],
      '@typescript-eslint/no-this-alias': 'error',
      '@typescript-eslint/no-unnecessary-type-assertion': 'error',
      '@typescript-eslint/no-unnecessary-type-constraint': 'error',
      '@typescript-eslint/no-unsafe-argument': 'error',
      '@typescript-eslint/no-unsafe-assignment': 'error',
      '@typescript-eslint/no-unsafe-call': 'error',
      '@typescript-eslint/no-unsafe-member-access': 'error',
      '@typescript-eslint/no-unsafe-return': 'error',
      '@typescript-eslint/no-unused-expressions': 'error',
      '@typescript-eslint/no-unused-vars': 'warn',
      '@typescript-eslint/no-use-before-define': 'off',
      '@typescript-eslint/no-wrapper-object-types': 'error',
      '@typescript-eslint/no-unsafe-function-type': 'error',
      '@typescript-eslint/prefer-as-const': 'error',
      '@typescript-eslint/prefer-for-of': 'error',
      '@typescript-eslint/prefer-function-type': 'error',
      '@typescript-eslint/prefer-namespace-keyword': 'error',
      '@typescript-eslint/require-await': 'error',
      '@typescript-eslint/restrict-plus-operands': 'error',
      '@typescript-eslint/restrict-template-expressions': 'error',
      '@typescript-eslint/triple-slash-reference': [
        'error',
        { path: 'always', types: 'prefer-import', lib: 'always' }
      ],
      '@typescript-eslint/unbound-method': 'error',
      '@typescript-eslint/unified-signatures': 'error',
      complexity: 'off',
      'constructor-super': 'error',
      'dot-notation': 'off',
      eqeqeq: ['error', 'smart'],
      'guard-for-in': 'error',
      'id-denylist': [
        'error',
        'any',
        'Number',
        'number',
        'String',
        'string',
        'Boolean',
        'boolean',
        'Undefined',
        'undefined'
      ],
      'id-match': 'error',
      'jsdoc/check-alignment': 'error',
      'jsdoc/check-indentation': 'error',
      'max-classes-per-file': ['error', 1],
      'new-parens': 'error',
      'no-array-constructor': 'off',
      'no-bitwise': 'error',
      'no-caller': 'error',
      'no-cond-assign': 'error',
      'no-console': 'off',
      'no-debugger': 'error',
      'no-empty': 'error',
      'no-empty-function': 'off',
      'no-eval': 'error',
      'no-extra-semi': 'error',
      'no-fallthrough': 'off',
      'no-implied-eval': 'off',
      'no-invalid-this': 'off',
      'no-loss-of-precision': 'error',
      'no-new-wrappers': 'error',
      'no-shadow': 'off',
      'no-throw-literal': 'error',
      'no-trailing-spaces': 'error',
      'no-undef-init': 'error',
      'no-underscore-dangle': 'off',
      'no-unsafe-finally': 'error',
      'no-unused-expressions': 'off',
      'no-unused-labels': 'error',
      'no-unused-vars': 'off',
      'no-use-before-define': 'off',
      'no-var': 'error',
      'object-shorthand': 'error',
      'one-var': ['error', 'never'],
      'prefer-arrow/prefer-arrow-functions': 'error',
      'prefer-const': 'error',
      radix: 'error',
      'require-await': 'off',
      'spaced-comment': ['error', 'always', { markers: ['/'] }],
      'use-isnan': 'error',
      'valid-typeof': 'off'
    }
  },

  {
    // Mock functions from jest-mock-extended / ts-mockito are not `this`-bound,
    // so handing one to expect() detaches nothing meaningful. The rule only
    // produces false positives here.
    files: ['**/*.spec.ts'],
    rules: {
      '@typescript-eslint/unbound-method': 'off'
    }
  },

  // Replaces the old "prettier" extends plus the prettier plugin entry: turns on
  // prettier/prettier and switches off every stylistic rule that would fight it.
  prettierRecommended
];
